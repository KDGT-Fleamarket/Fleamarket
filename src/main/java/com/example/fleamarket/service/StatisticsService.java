package com.example.fleamarket.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.fleamarket.repository.AppOrderRepository;
import com.example.fleamarket.repository.ItemRepository;
import com.example.fleamarket.repository.LoginLogRepository;
import com.example.fleamarket.repository.UserRepository;

@Service
public class StatisticsService {
	private final AppOrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final LoginLogRepository loginLogRepository;

	public StatisticsService(AppOrderRepository orderRepository,
			UserRepository userRepository,
			ItemRepository itemRepository,
			LoginLogRepository loginLogRepository) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
		this.loginLogRepository = loginLogRepository;
	}

	// 売上金額・取引件数
	public Map<String, Object> getRevenueChartData(LocalDate start, LocalDate end) {
		// 検索範囲を LocalDateTime に変換
		LocalDateTime startDateTime = start.atStartOfDay();
		LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

		List<Object[]> results = orderRepository.findDailySales(startDateTime, endDateTime);

		// DBの結果をMapに格納（日付 -> 値）
		Map<LocalDate, BigDecimal> salesMap = new HashMap<>();
		Map<LocalDate, Long> countMap = new HashMap<>();

		for (Object[] row : results) {
			LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
			salesMap.put(date, (BigDecimal) row[1]);
			countMap.put(date, (Long) row[2]);
		}

		// グラフ用のラベル（日付）とデータ（売上・件数）を作成
		List<String> labels = new ArrayList<>();
		List<BigDecimal> salesData = new ArrayList<>();
		List<Long> countData = new ArrayList<>();

		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			labels.add(date.toString());
			salesData.add(salesMap.getOrDefault(date, BigDecimal.ZERO));
			countData.add(countMap.getOrDefault(date, 0L));
		}

		Map<String, Object> chartData = new HashMap<>();
		chartData.put("labels", labels);
		chartData.put("sales", salesData);
		chartData.put("counts", countData);
		return chartData;
	}

	// 新規登録者数・新規出品数データ
	public Map<String, Object> getActivityChartData(LocalDate start, LocalDate end) {
		LocalDateTime startDT = start.atStartOfDay();
		LocalDateTime endDT = end.atTime(LocalTime.MAX);

		List<Object[]> userResults = userRepository.countDailyRegistrations(startDT, endDT);
		List<Object[]> itemResults = itemRepository.countDailyItems(startDT, endDT);

		Map<LocalDate, Long> userMap = new HashMap<>();
		Map<LocalDate, Long> itemMap = new HashMap<>();

		userResults.forEach(r -> userMap.put(((java.sql.Date) r[0]).toLocalDate(), (Long) r[1]));
		itemResults.forEach(r -> itemMap.put(((java.sql.Date) r[0]).toLocalDate(), (Long) r[1]));

		List<String> labels = new ArrayList<>();
		List<Long> userData = new ArrayList<>();
		List<Long> itemData = new ArrayList<>();

		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			labels.add(date.toString());
			userData.add(userMap.getOrDefault(date, 0L));
			itemData.add(itemMap.getOrDefault(date, 0L));
		}

		Map<String, Object> chartData = new HashMap<>();
		chartData.put("labels", labels);
		chartData.put("users", userData);
		chartData.put("items", itemData);
		return chartData;
	}

	// カテゴリ別売上グラフデータ
	public Map<String, Object> getCategoryChartData(LocalDate start, LocalDate end) {
		LocalDateTime startDT = start.atStartOfDay();
		LocalDateTime endDT = end.atTime(LocalTime.MAX);

		List<Object[]> results = orderRepository.findSalesByCategory(startDT, endDT);

		List<String> labels = new ArrayList<>();
		List<BigDecimal> data = new ArrayList<>();

		for (Object[] row : results) {
			labels.add((String) row[0]); // カテゴリ名
			data.add((BigDecimal) row[1]); // 売上合計
		}

		Map<String, Object> chartData = new HashMap<>();
		chartData.put("labels", labels);
		chartData.put("data", data);
		return chartData;
	}

	// カテゴリ別出品数データ
	public Map<String, Object> getItemCategoryChartData(LocalDate start, LocalDate end) {
		LocalDateTime startDT = start.atStartOfDay();
		LocalDateTime endDT = end.atTime(LocalTime.MAX);

		List<Object[]> results = itemRepository.findItemCountByCategory(startDT, endDT);

		List<String> labels = new ArrayList<>();
		List<Long> data = new ArrayList<>();

		for (Object[] row : results) {
			labels.add((String) row[0]);
			data.add((Long) row[1]);
		}

		Map<String, Object> chartData = new HashMap<>();
		chartData.put("labels", labels);
		chartData.put("data", data);
		return chartData;
	}

	// アクティブユーザー数データ
	public Map<String, Object> getActiveUserChartData(LocalDate start, LocalDate end) {
		LocalDateTime startDT = start.atStartOfDay();
		LocalDateTime endDT = end.atTime(LocalTime.MAX);

		List<Object[]> results = loginLogRepository.countDailyActiveUsers(startDT, endDT);

		// 検索しやすいように一旦Map（日付 -> 人数）に変換
		Map<LocalDate, Long> activeMap = new HashMap<>();
		results.forEach(r -> activeMap.put(((java.sql.Date) r[0]).toLocalDate(), (Long) r[1]));

		List<String> labels = new ArrayList<>();
		List<Long> data = new ArrayList<>();

		// 開始日から終了日まで1日ずつループしてリストを作成
		for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
			labels.add(d.toString());
			data.add(activeMap.getOrDefault(d, 0L)); // データがない日は0を入れる
		}

		Map<String, Object> chartData = new HashMap<>();
		chartData.put("labels", labels);
		chartData.put("data", data);
		return chartData;
	}
}