//資料P66
package com.example.fleamarket.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarket.entity.AppOrder;
import com.example.fleamarket.entity.Item;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.AppOrderRepository;
import com.example.fleamarket.repository.ItemRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@Service
public class AppOrderService {

	private final AppOrderRepository appOrderRepository;
	private final ItemRepository itemRepository;
	private final ItemService itemService;
	private final StripeService stripeService;
	private final LineNotifyService lineNotifyService;

	public AppOrderService(AppOrderRepository appOrderRepository, ItemRepository itemRepository,
			ItemService itemService, StripeService stripeService, LineNotifyService lineNotifyService) {
		this.appOrderRepository = appOrderRepository;
		this.itemRepository = itemRepository;
		this.itemService = itemService;
		this.stripeService = stripeService;
		this.lineNotifyService = lineNotifyService;
	}

	@Transactional
	public PaymentIntent initiatePurchase(Long itemId, User buyer) throws StripeException {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Item not found"));

		if (!"出品中".equals(item.getStatus())) {
			throw new IllegalStateException("Item is not available for purchase.");
		}

		// Create a PaymentIntent with Stripe
		PaymentIntent paymentIntent = stripeService.createPaymentIntent(item.getPrice(), "jpy",
				"購入: " + item.getName());

		// Save a pending order (or create it after successful payment confirmation)
		// For simplicity, we'll create the order here and update its status later
		AppOrder appOrder = new AppOrder();
		appOrder.setItem(item);
		appOrder.setBuyer(buyer);
		appOrder.setPrice(item.getPrice());
		appOrder.setStatus("決済待ち"); // New status for pending payment
		appOrder.setPaymentIntentId(paymentIntent.getId());
		appOrder.setCreatedAt(LocalDateTime.now()); // Set creation time
		appOrderRepository.save(appOrder);

		return paymentIntent;
	}

	@Transactional
	public AppOrder completePurchase(String paymentIntentId) throws StripeException {
		PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

		if ("succeeded".equals(paymentIntent.getStatus())) {
			// Find the order associated with this payment intent (you might need to store paymentIntentId in AppOrder entity)
			// For now, let's assume we find the latest pending order for simplicity
			AppOrder appOrder = appOrderRepository.findAll().stream()
					.filter(o -> paymentIntentId.equals(o.getPaymentIntentId()))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("No pending order found for this payment."));

			appOrder.setStatus("購入済");
			itemService.markItemAsSold(appOrder.getItem().getId());
			AppOrder savedOrder = appOrderRepository.save(appOrder);

			// Send LINE notification to seller
			//			if (savedOrder.getItem().getSeller().getLineNotifyToken() != null) {
			//				String message = String.format("\n商品が購入されました！\n商品名: %s\n購入者: %s\n価格: ¥%s",
			//						savedOrder.getItem().getName(),
			//						savedOrder.getBuyer().getName(),
			//						savedOrder.getPrice());
			//				lineNotifyService.sendMessage(savedOrder.getItem().getSeller().getLineNotifyToken(), message);
			//			}

			return savedOrder;
		} else {
			throw new IllegalStateException("Payment not succeeded. Status: " + paymentIntent.getStatus());
		}
	}

	public List<AppOrder> getAllOrders() {
		return appOrderRepository.findAll();
	}

	public List<AppOrder> getOrdersByBuyer(User buyer) {
		return appOrderRepository.findByBuyer(buyer);
	}

	public List<AppOrder> getOrdersBySeller(User seller) {
		return appOrderRepository.findByItem_Seller(seller);
	}

	@Transactional
	public void markOrderAsShipped(Long orderId) {
		AppOrder appOrder = appOrderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));
		appOrder.setStatus("発送済");
		AppOrder savedOrder = appOrderRepository.save(appOrder);

		//		// Send LINE notification to buyer
		//		if (savedOrder.getBuyer().getLineNotifyToken() != null) {
		//			String message = String.format("\n購入した商品が発送されました！\n商品名: %s\n出品者: %s",
		//					savedOrder.getItem().getName(),
		//					savedOrder.getItem().getSeller().getName());
		//			lineNotifyService.sendMessage(savedOrder.getBuyer().getLineNotifyToken(), message);
		//		}
	}

	public AppOrder getOrderById(Long orderId) {
		return appOrderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("注文が見つかりません ID: " + orderId));
	}

	public Optional<Long> getLatestCompletedOrderId() {
		return appOrderRepository.findAll().stream()
				.filter(o -> "購入済".equals(o.getStatus()))
				.map(AppOrder::getId)
				.max(Long::compare);
	}

	public BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate) {
		return appOrderRepository.findAll().stream()
				.filter(order -> order.getStatus().equals("購入済") || order.getStatus().equals("発送済"))
				.filter(order -> order.getCreatedAt().toLocalDate().isAfter(startDate.minusDays(1))
						&& order.getCreatedAt().toLocalDate().isBefore(endDate.plusDays(1))) // Use order.getCreatedAt()
				.map(AppOrder::getPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public Map<String, Long> getOrderCountByStatus(LocalDate startDate, LocalDate endDate) {
		return appOrderRepository.findAll().stream()
				.filter(order -> order.getCreatedAt().toLocalDate().isAfter(startDate.minusDays(1))
						&& order.getCreatedAt().toLocalDate().isBefore(endDate.plusDays(1))) // Use order.getCreatedAt()
				.collect(Collectors.groupingBy(AppOrder::getStatus, Collectors.counting()));
	}

	@Transactional
	@Scheduled(fixedRate = 600000) // 10分ごとに実行 (単位: ミリ秒)
	public void deleteExpiredOrders() {
		LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
		// 「決済待ち」かつ「30分以上前」の注文を取得
		List<AppOrder> expiredOrders = appOrderRepository.findAll().stream()
				.filter(o -> "決済待ち".equals(o.getStatus()))
				.filter(o -> o.getCreatedAt().isBefore(threshold))
				.toList();

		if (!expiredOrders.isEmpty()) {
			for (AppOrder order : expiredOrders) {

				appOrderRepository.delete(order);
			}
			System.out.println(expiredOrders.size() + "件の期限切れ注文を削除しました。");
		}
	}
}