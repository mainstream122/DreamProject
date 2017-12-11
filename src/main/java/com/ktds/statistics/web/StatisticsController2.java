package com.ktds.statistics.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ktds.hdfs.ReadAccessLog;
import com.ktds.hdfs.ReadRemoteFile;
import com.ktds.statistics.vo.StatisticsVO;

import scala.Tuple3;

@Controller
public class StatisticsController2 {
	@RequestMapping("/statistics2")
	public ModelAndView viewStatisticsPage() {
		ModelAndView view = new ModelAndView();
		
		String[] logs = ReadRemoteFile.get("z://kdg", file -> file.getName().startsWith("part-")).split("\n");
		
		List<StatisticsVO> statisticsList = Arrays.asList(logs)
		.stream()
		.filter(row -> {
			System.out.println(row);
			boolean isLogData = row.split(",").length == 3;
			boolean isLogData2 = row.startsWith("20");
			return isLogData && isLogData2;
		})
		.filter(row -> row.split(",")[2].trim().matches("[0-9]+"))
		.map(row -> { 
			StatisticsVO vo = new StatisticsVO();
			vo.setDateTime(row.split(",")[0]);
			vo.setUserName(row.split(",")[1]);
			vo.setCount(Integer.parseInt(row.split(",")[2])); 
			return vo;
		})
		.collect(Collectors.toList());
		
		
		Map<String, Map<String, Integer>> groupData = 
				statisticsList.stream()
							  .collect(
								  Collectors.groupingBy(StatisticsVO::getDateTime, 
										  Collectors.groupingBy(StatisticsVO::getUserName
												  , Collectors.reducing(0, StatisticsVO::getCount, (int1, int2) -> int1+ int2))));
		
		//List<Tuple3<String, String, Integer>> dataTuple = new ArrayList();
		List<String> dates = new ArrayList<>();
		List<String> users = new ArrayList<>();
		
		groupData.forEach((key, map) -> {
			if(!dates.contains(key)) {
				dates.add(key);
			}
			map.forEach((key2, count) -> {
				if(!users.contains(key2)) {
					users.add(key2);
				}
				//dataTuple.add(new Tuple3<>(key, key2, count));
			});
		});
		
		
		dates.sort((d1, d2) -> {
			return d1.compareTo(d2);
		});
		users.sort((d1, d2) -> {
			return d1.compareTo(d2);
		});
		
		
		String category = dates.stream().map(d-> "'" + d + "'").collect(Collectors.joining(","));
		String data = "";
		for(String usr : users) {
			data += (data.length() > 0 ? ", [" : "[");
			data += "'" + usr + "'";
			for (String dt : dates) {
				data += ", " + (groupData.containsKey(dt) && groupData.get(dt).containsKey(usr) ? groupData.get(dt).get(usr) : 0) + "";
			}
			data += "]";
		}
		
		view.addObject("data", data);
		view.addObject("category", "[" + category + "]");
		view.addObject("statisticsList", statisticsList);
		view.addObject("groupData", groupData);
		view.setViewName("statistics/statistics2");
		return view;
	}
}