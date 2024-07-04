package com.example.member.service;

import com.example.member.DemoApplication;
import com.example.member.data.SubwayData;
import com.example.member.data.TranslineData;
import com.example.member.dto.PathWithLength;
import com.example.member.dto.TransferStation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.member.domain.InputData;
import com.example.member.domain.TransferInfo;
import com.example.member.domain.OutputData;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
public class FindRouteService {

    private static Map<String, Map<String, Integer>> subwayGraph;
    private static List<Map<String, Object>> transline;
    private static List<Map<String, Object>> line1;
    private static List<Map<String, Object>> line2;
    private static List<Map<String, Object>> line3;
    private static List<Map<String, Object>> line4;
    private static List<Map<String, Object>> line5;
    private static List<Map<String, Object>> line6;
    private static List<Map<String, Object>> line7;
    private static List<Map<String, Object>> line8;

    private static List<List<Map<String, Object>>> lines;

    static {
        transline = TranslineData.allData();
        line1 = SubwayData.getLine1Data();
        line2 = SubwayData.getLine2Data();
        line3 = SubwayData.getLine3Data();
        line4 = SubwayData.getLine4Data();
        line5 = SubwayData.getLine5Data();
        line6 = SubwayData.getLine6Data();
        line7 = SubwayData.getLine7Data();
        line8 = SubwayData.getLine8Data();

        lines = Arrays.asList(line1, line2, line3, line4, line5, line6, line7, line8);
        subwayGraph = Graph.createGraph(lines);
    }


    public List<OutputData> calculateTimeForPath(String start, String end, Integer time,String dayType) {

        List<OutputData> outputs = new ArrayList<>();
        List<PathWithLength> pathWithLengths = dijkstraAllPathsWithLength(start, end);
        Collections.sort(pathWithLengths, Comparator.comparingInt(PathWithLength::getLength));
        // 길이를 기준으로 경로를 정렬
        int count = 0;
        for (PathWithLength pathWithLength : pathWithLengths)
        {
            List<String> path = pathWithLength.getPath();

            pathWithLength.setLength(calculatePathDistance(path));
            pathWithLength.setBaseline(Graph.getBaseLine(lines,path));
        }
        Collections.sort(pathWithLengths, Comparator.comparingInt(PathWithLength::getLength));

        for (PathWithLength pathWithLength : pathWithLengths) {
            if(count == 3)
                break;
            List<String> path = pathWithLength.getPath();

            int times = pathWithLength.getLength();
            //System.out.println("시간표 반영 전 걸리는 시간 = " + times / 60 + "분 " + times % 60 + "초");

            //LocalDateTime now = LocalDateTime.now();
            LocalDate today = LocalDate.now();

            // 자정 시간 (00:00:00)을 기준으로 time 초를 더한 LocalDateTime 객체를 생성
            LocalDateTime now = LocalDateTime.of(today, LocalTime.MIDNIGHT).plusSeconds(time);

            int secondsSinceMidnight = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
            LocalDateTime tmp = now;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            //System.out.println("현재 시간: " + formattedDateTime);

            //System.out.println("path = " + path);
            List<TransferInfo> transferStations = checkTranslate(path);


            Map<String, Integer> distancesToTransfers = calculateDistanceToTransfers(path, transferStations);

            List<Integer> EachTime = new ArrayList<>();
            List<Integer> EachWalkingTime = new ArrayList<>();
            List<String> EachTypeOfLine = new ArrayList<>();
            List<Integer> EachWaitingTime = new ArrayList<>();
            List<String> EachTransferStation = new ArrayList<>();

            //System.out.println("처음 타는 호선 = " + pathWithLength.getBaseline());
            String StartLine = pathWithLength.getBaseline();

            for (TransferInfo transferInfo : transferStations) {
                AtomicReference<String> type = new AtomicReference<>();

                EachTransferStation.add(transferInfo.getStation());
                EachWalkingTime.add(transferInfo.getTransferTime());
                //System.out.println("환승역: " + transferInfo.getStation() + ", 환승시간: " + transferInfo.getTransferTime() / 60 + "분 " + transferInfo.getTransferTime() % 60 + "초");

                //System.out.println("다음역: " + transferInfo.getNextStation());

                transline.stream()
                        .filter(entry -> entry.get("current").equals(transferInfo.getStation()) &&
                                entry.get("next").equals(transferInfo.getNextStation()) &&
                                entry.get("transline").equals(transferInfo.getToLine()))
                        .forEach(entry -> type.set((String) entry.get("type")));

                //System.out.println("환승역까지 걸리는 시간: " + distancesToTransfers.get(transferInfo.getStation()) + "초");
                EachTime.add(distancesToTransfers.get(transferInfo.getStation()));

                int currentTimeInSeconds = now.toLocalTime().toSecondOfDay();
                int totalSeconds = currentTimeInSeconds + distancesToTransfers.get(transferInfo.getStation()) + transferInfo.getTransferTime();
                int hours = totalSeconds / 3600;
                int minutes = (totalSeconds % 3600) / 60;
                int seconds = totalSeconds % 60;

                //System.out.println("환승역 도착 시간: " + hours + "시 " + minutes + "분 " + seconds + "초");
                //System.out.println("출발 호선: " + transferInfo.getFromLine() + ", 도착 호선: " + transferInfo.getToLine());
                EachTypeOfLine.add(transferInfo.getToLine());

                now = now.withHour(hours).withMinute(minutes).withSecond(seconds);
                int day = 0;
                if(dayType.equals("평일"))
                    day = 1;
                else if(dayType.equals("토요일"))
                    day = 2;
                else if(dayType.equals("일요일"))
                    day = 3;
                String apiUrl = "http://openapi.seoul.go.kr:8088/6e4654576264677334306b73585575/json/SearchSTNTimeTableByIDService/1/500/" + getCodeByLine(transferInfo.getToLine(), transferInfo.getStation()) + "/"+day+"/" + type;

                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper objectMapper = new ObjectMapper();

                int differenceInSeconds = 0;
                try {
                    String response = restTemplate.getForObject(apiUrl, String.class);
                    JsonNode root = objectMapper.readTree(response);

                    boolean found = false;

                    for (JsonNode row : root.path("SearchSTNTimeTableByIDService").path("row")) {
                        String leftTime = row.path("LEFTTIME").asText();
                        if (leftTime == null || leftTime.isEmpty()) {
                            continue; // 'LEFTTIME' 필드가 없거나 비어 있으면 건너뜁니다.
                        }

                        LocalTime leftLocalTime;
                        try {
                            leftLocalTime = LocalTime.parse(leftTime);
                        } catch (DateTimeParseException e) {
                            continue; // 'LEFTTIME'이 올바른 형식이 아니면 건너뜁니다.
                        }

                        LocalTime transferLocalTime = LocalTime.of(hours, minutes, seconds);

                        if (leftLocalTime.isAfter(transferLocalTime)) {
                            int leftTimeInSeconds = leftLocalTime.toSecondOfDay();
                            int transferTimeInSeconds = transferLocalTime.toSecondOfDay();

                            differenceInSeconds = leftTimeInSeconds - transferTimeInSeconds;

                            EachWaitingTime.add(differenceInSeconds);
                            times += differenceInSeconds;
                            now = now.toLocalDate().atTime(leftLocalTime);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // `LEFTTIME`을 찾지 못했을 경우
                        differenceInSeconds = 0;
                        EachWaitingTime.add(differenceInSeconds);
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }

            Integer TotalTime = times;


            //System.out.println("시간표 반영 후 걸리는 시간 = " + times / 60 + "분 " + times % 60 + "초");

            LocalDateTime finalDateTime = tmp.plusSeconds(times);
            String finalFormattedDateTime = finalDateTime.format(timeFormatter);

            String FinalTime = finalFormattedDateTime;
            //System.out.println("예상 최종 시간: " + finalFormattedDateTime);
            //System.out.println();
            count++;
            OutputData output = new OutputData(secondsSinceMidnight,path,StartLine,EachTime,EachWalkingTime,EachTypeOfLine,EachTransferStation,EachWaitingTime,TotalTime,FinalTime,transferStations);

            outputs.add(output);
        }

        return outputs;
    }
    public List<OutputData> calculateAI(List<OutputData> outputData,String dayType) {

        List<OutputData> outputs = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        List<Integer> aitime = null;
        List<String> eachpath = null;
        for (OutputData data : outputData) {
            try {
                // eachpath를 가져옴
                eachpath = data.getPath();


                // 요청 데이터 준비
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("path", eachpath);

                // HTTP 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // 요청 데이터를 HttpEntity로 감싸기
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                // REST API 호출
                String postUrl = "http://flask:8082/getPred";
                ResponseEntity<String> responseEntity = restTemplate.exchange(postUrl, HttpMethod.POST, requestEntity, String.class);
                String responseBody = responseEntity.getBody();
                System.out.println("response" + responseBody);


                // JSON 데이터 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // subway_start 추출
                String subwayStart = jsonNode.get("subway_start").asText();

                // 소요시간 데이터 추출
                JsonNode 소요시간Array = jsonNode.get("data");
                aitime = new ArrayList<>();
                for (JsonNode node : 소요시간Array) {
                    int 소요시간 = node.asInt();
                    aitime.add(소요시간);
                }
            } catch (JsonMappingException e) {
                // JSON 매핑 중 예외 발생 시 처리
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                // JSON 파싱 중 예외 발생 시 처리
                e.printStackTrace();
            }


            outputs.add(data);

            int a = 0;
            int total = 0;
            int index = 0;
            List<String> eachTransStaion = data.getEachTransferStation();
            List<Integer> eachtime = data.getEachTime();
            List<Integer> eachwaitingtime = data.getEachWaitingTime();
            List<Integer> eachwalkingtime = data.getEachWalkingTime();
            // EachTransferStation 리스트를 순회하면서 작업 수행
//            for (String path : eachpath) {
//                int index = 0;
//                for (String TransStation : eachTransStaion) {
//                    if(a==eachTransStaion.size())
//                        break;
//
//                    if (TransStation.equals(path)) {
//                        eachtime.remove(a);
//                        eachtime.add(a, total);
//                        total=0;
//                        a++;
//                        break;
//                    }
//
//                    // int index = eachpath.indexOf(path)
//
//
//                }
//                total += aitime.get(index);
//                System.out.println(total);
//
//                index+=1;
//                if(index==eachpath.size()-1) {
//                    total = 0;
//                    break;
//                }
//
//            }

            for (String path : eachpath){

                if(a==eachTransStaion.size())
                    break;

                if (eachTransStaion.get(a).equals(path)) {
                        eachtime.remove(a);
                        eachtime.add(a, total);
                        total=0;
                        a++;
                }

                total+=aitime.get(index);
                index++;
            }

            int secondsSinceMidnight = data.getCurtime();

            //List<TransferInfo> transferStations = checkTranslate(path);

            //Map<String, Integer> distancesToTransfers = calculateDistanceToTransfers(path, transferStations);

            List<TransferInfo> transferInfos = data.getTransferStations();

            int i = 0;
            for (TransferInfo transferInfo : transferInfos) {

                AtomicReference<String> type = new AtomicReference<>();

                transline.stream()
                        .filter(entry -> entry.get("current").equals(transferInfo.getStation()) &&
                                entry.get("next").equals(transferInfo.getNextStation()) &&
                                entry.get("transline").equals(transferInfo.getToLine()))
                        .forEach(entry -> type.set((String) entry.get("type")));


                int currentTimeInSeconds = data.getCurtime();
                int totalSeconds = currentTimeInSeconds + eachtime.get(i) + transferInfo.getTransferTime();
                int hours = totalSeconds / 3600;
                int minutes = (totalSeconds % 3600) / 60;
                int seconds = totalSeconds % 60;


                int day = 0;
                if(dayType.equals("평일"))
                    day = 1;
                else if(dayType.equals("토요일"))
                    day = 2;
                else if(dayType.equals("일요일"))
                    day = 3;

                String apiUrl = "http://openapi.seoul.go.kr:8088/6e4654576264677334306b73585575/json/SearchSTNTimeTableByIDService/1/500/" + getCodeByLine(transferInfo.getToLine(), transferInfo.getStation()) + "/"+day+"/" + type;


                RestTemplate restTemplate2 = new RestTemplate();
                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    String response = restTemplate2.getForObject(apiUrl, String.class);
                    JsonNode root = objectMapper.readTree(response);

                    for (JsonNode row : root.path("SearchSTNTimeTableByIDService").path("row")) {
                        String leftTime = row.path("LEFTTIME").asText();
                        LocalTime leftLocalTime = LocalTime.parse(leftTime);
                        LocalTime transferLocalTime = LocalTime.of(hours, minutes, seconds);

                        if (leftLocalTime.isAfter(transferLocalTime)) {

                            int leftTimeInSeconds = leftLocalTime.toSecondOfDay();
                            int transferTimeInSeconds = transferLocalTime.toSecondOfDay();
                            int differenceInSeconds = leftTimeInSeconds - transferTimeInSeconds;

                            //System.out.println("시간 차이: " + differenceInSeconds + "초");
                            eachwaitingtime.set(i,differenceInSeconds);

                            break;
                        }
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                i++;

            }
            int totaltime = 0;
            for (int waitingTime : eachwaitingtime) {
                totaltime += waitingTime;
            }

            for (int walkingTime : eachwalkingtime) {
                totaltime += walkingTime;
            }

            for (int aiTime : aitime) {
                System.out.println(aitime);
                totaltime += aiTime;
            }

            data.setTotalTime(totaltime);

        }

        return outputs;
    }

    private static String getCodeByLine(String line,String name) {
        List<Map<String, Object>> lineData = null;
        // 호선에 따라 해당하는 데이터를 가져오도록 분기 처리합니다.
        switch (line) {
            case "1호선":
                lineData = SubwayData.getLine1Data();
                break;
            case "2호선":
                lineData = SubwayData.getLine2Data();
                break;
            case "3호선":
                lineData = SubwayData.getLine3Data();
                break;
            case "4호선":
                lineData = SubwayData.getLine4Data();
                break;
            case "5호선":
                lineData = SubwayData.getLine5Data();
                break;
            case "6호선":
                lineData = SubwayData.getLine6Data();
                break;
            case "7호선":
                lineData = SubwayData.getLine7Data();
                break;
            case "8호선":
                lineData = SubwayData.getLine8Data();
                break;

            default:
                return "호선 데이터 없음";
        }

        for (Map<String, Object> station : lineData) {
            if (line.equals(station.get("line"))) {
                if(name.equals(station.get("name"))) {
                    return (String) station.get("code");
                }
            }
        }
        return "코드 없음";
    }
    private static Map<String, Integer> calculateDistanceToTransfers(List<String> path, List<TransferInfo> transferStations) {
        Map<String, Integer> distances = new HashMap<>();

        for (int t = 0; t < transferStations.size(); t++) {
            TransferInfo transferInfo = transferStations.get(t);
            String transferStation = transferInfo.getStation();
            int distance = 0;

            // 현재 환승역에서 다음 환승역까지의 거리를 구하기 위해 출발지점 설정
            String startStation;
            if (t == 0) {
                startStation = path.get(0); // 첫 번째 환승역까지는 출발지에서 시작
            } else {
                startStation = transferStations.get(t - 1).getStation(); // 그 외는 이전 환승역에서 시작
            }

            String base_line = null;
            boolean transferReached = false;

            // 이전 환승역(startStation)에서 현재 환승역(transferStation)까지의 거리 계산
            for (int i = path.indexOf(startStation); i < path.size() - 1; i++) {
                String first = path.get(i);
                String next = path.get(i + 1);

                Integer edgeWeight = subwayGraph.get(first).get(next);
                if (edgeWeight == null) {
                    throw new RuntimeException("환승역까지의 거리를 찾을 수 없습니다.");
                }

                for (List<Map<String, Object>> line : lines) {
                    for (Map<String, Object> station : line) {
                        if (first.equals(station.get("name"))) {
                            if (station.get("next") instanceof List) {
                                List<?> list = (List<?>) station.get("next");
                                for (Object item : list) {
                                    String newer = (String) item;
                                    String[] split_data = newer.split(",");
                                    String tmp_next = split_data[0];

                                    if (next.equals(tmp_next) && i == path.indexOf(startStation)) {
                                        base_line = (String) station.get("line");
                                        break;
                                    } else if (next.equals(tmp_next) && base_line != null && !base_line.equals(station.get("line"))) {
                                        String tmp_line = (String) station.get("line");
                                        for (Map<String, Object> transline_data : transline) {
                                            if (first.equals(transline_data.get("current")) && next.equals(transline_data.get("next"))) {
                                                if (tmp_line.equals(transline_data.get("transline"))) {
                                                    String times = (String) transline_data.get("time");
                                                    String[] parts = times.split(":");
                                                    int convert_time = convertToSeconds(parts[0], parts[1]);
                                                    //distance += convert_time + 300; // 환승 시간 추가
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                String newer = (String) station.get("next");
                                String[] split_data = newer.split(",");
                                String tmp_next = split_data[0];

                                if (next.equals(tmp_next) && i == path.indexOf(startStation)) {
                                    base_line = (String) station.get("line");
                                    break;
                                } else if (next.equals(tmp_next) && base_line != null && !base_line.equals(station.get("line"))) {
                                    String tmp_line = (String) station.get("line");
                                    for (Map<String, Object> transline_data : transline) {
                                        if (first.equals(transline_data.get("current")) && next.equals(transline_data.get("next"))) {
                                            if (tmp_line.equals(transline_data.get("transline"))) {
                                                String times = (String) transline_data.get("time");
                                                String[] parts = times.split(":");
                                                int convert_time = convertToSeconds(parts[0], parts[1]);
                                                //distance += convert_time + 300; // 환승 시간 추가
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                distance += edgeWeight;

                if (next.equals(transferStation)) {
                    distances.put(transferStation, distance);
                    transferReached = true;
                    break;
                }
            }

            if (!transferReached) {
                throw new RuntimeException("환승역까지의 거리를 찾을 수 없습니다.");
            }
        }

        return distances;
    }


    private static int convertToSeconds(String minutes, String seconds) {
        // 분과 초를 정수로 변환하고 초로 합산
        int min = Integer.parseInt(minutes);
        int sec = Integer.parseInt(seconds);
        return min * 60 + sec;
    }



    public static List<PathWithLength> dijkstraAllPathsWithLength(String start, String end) { //모든 경로 다익스트라
        List<PathWithLength> allPaths = new ArrayList<>();
        Queue<PathWithLength> queue = new LinkedList<>();
        queue.add(new PathWithLength(Collections.singletonList(start), 0));
        int count = 0;

        while (!queue.isEmpty()) {
            if(count == 5000)
                break;
            PathWithLength pathWithLength = queue.poll();
            List<String> path = pathWithLength.getPath();
            String current = path.get(path.size() - 1);

            if (current.equals(end)) {
                allPaths.add(pathWithLength);
                count++;
            } else {
                Map<String, Integer> neighbors = subwayGraph.getOrDefault(current, new HashMap<>());
                for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                    String neighbor = neighborEntry.getKey();
                    if (!path.contains(neighbor)) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(neighbor);
                        queue.add(new PathWithLength(newPath,pathWithLength.getLength() + 1));
                    }
                }
            }
        }

        return allPaths;
    }

    public static List<TransferInfo> checkTranslate(List<String> path) {  //환승역 구하는 코드
        List<TransferInfo> transferStations = new ArrayList<>();
        String baseLine = null;

        for (int i = 0; i < path.size() - 1; i++) {
            String first = path.get(i);
            String next = path.get(i + 1);

            Integer edgeWeight = subwayGraph.get(first).get(next);

            for (List<Map<String, Object>> line : lines)
            {
                int flag =0;
                for (Map<String, Object> station : line) {
                    if (first.equals((String) station.get("name"))) {
                        if (station.get("next") instanceof List)
                        {
                            List<?> list = (List<?>) station.get("next");
                            for (int z = 0; z < list.size(); z++)
                            {
                                String newer = (String) list.get(z);
                                String[] splitData = newer.split(",");
                                String tmpNext = splitData[0];
                                if (next.equals(tmpNext) && i == 0)
                                {
                                    baseLine = (String) station.get("line");
                                    flag = 1;
                                    break;
                                }
                                else if (next.equals(tmpNext) && (baseLine.equals((String) station.get("line"))))
                                {
                                    flag = 1;
                                    break;
                                }
                                else if (next.equals(tmpNext) && !(baseLine.equals((String) station.get("line")))) {
                                    String tmpLine = (String) station.get("line");

                                    for (Map<String, Object> translineData : transline) {
                                        if (first.equals((String) translineData.get("current")) && next.equals((String) translineData.get("next"))) {
                                            if (tmpLine.equals((String) translineData.get("transline"))) {
                                                String times = (String) translineData.get("time");
                                                String[] parts = times.split(":");
                                                int convertTime = convertToSeconds(parts[0], parts[1]);
                                                String fromLine = baseLine;
                                                String toLine = tmpLine;
                                                transferStations.add(new TransferInfo(first, next, fromLine, toLine, convertTime));
                                                flag=1;
                                                break;
                                            }
                                        }
                                    }
                                    baseLine = tmpLine;



                                }
                                if(flag==1)
                                    break;
                            }
                        }
                        else
                        {
                            String newer = (String) station.get("next");
                            String[] splitData = newer.split(",");
                            String tmpNext = splitData[0];

                            if (next.equals(tmpNext) && i == 0) {
                                baseLine = (String) station.get("line");
                                flag = 1;
                                break;
                            } else if (next.equals(tmpNext) && (baseLine.equals((String) station.get("line"))))
                            {
                                flag = 1;
                                break;
                            }
                            else if (next.equals(tmpNext) && !(baseLine.equals((String) station.get("line"))))
                            {
                                String tmpLine = (String) station.get("line");

                                for (Map<String, Object> translineData : transline) {
                                    if (first.equals((String) translineData.get("current")) && next.equals((String) translineData.get("next"))) {
                                        if (tmpLine.equals((String) translineData.get("transline"))) {
                                            String times = (String) translineData.get("time");
                                            String[] parts = times.split(":");
                                            int convertTime = convertToSeconds(parts[0], parts[1]);
                                            String fromLine = baseLine;
                                            String toLine = tmpLine;
                                            transferStations.add(new TransferInfo(first, next, fromLine, toLine, convertTime));
                                            flag=1;
                                            break;
                                        }
                                    }
                                }

                                baseLine = tmpLine;


                            }
                            if(flag==1)
                                break;
                        }

                        if (station.get("prev") instanceof List) {
                            List<?> list = (List<?>) station.get("prev");
                            for (int z = 0; z < list.size(); z++) {
                                String newer = (String) list.get(z);
                                String[] splitData = newer.split(",");
                                String tmpPrev = splitData[0];

                                if (next.equals(tmpPrev) && i == 0) {
                                    baseLine = (String) station.get("line");
                                    flag = 1;
                                    break;
                                } else if (next.equals(tmpPrev) && (baseLine.equals((String) station.get("line"))))
                                {
                                    flag = 1;
                                    break;
                                }
                                else if (next.equals(tmpPrev) && !(baseLine.equals((String) station.get("line")))) {
                                    String tmpLine = (String) station.get("line");
                                    for (Map<String, Object> translineData : transline) {
                                        if (first.equals((String) translineData.get("current")) && next.equals((String) translineData.get("next"))) {
                                            if (tmpLine.equals((String) translineData.get("transline"))) {
                                                String times = (String) translineData.get("time");
                                                String[] parts = times.split(":");
                                                int convertTime = convertToSeconds(parts[0], parts[1]);
                                                String fromLine = baseLine;
                                                String toLine = tmpLine;
                                                transferStations.add(new TransferInfo(first, next, fromLine, toLine, convertTime));
                                                flag=1;
                                                break;
                                            }
                                        }
                                    }
                                    baseLine = tmpLine;


                                }
                                if(flag==1)
                                    break;
                            }
                        } else {
                            String newer = (String) station.get("prev");
                            String[] splitData = newer.split(",");
                            String tmpPrev = splitData[0];

                            if (next.equals(tmpPrev) && i == 0) {
                                baseLine = (String) station.get("line");
                                flag = 1;
                                break;
                            }  else if (next.equals(tmpPrev) && (baseLine.equals((String) station.get("line"))))
                            {
                                flag = 1;
                                break;
                            }
                            else if (next.equals(tmpPrev) && !(baseLine.equals((String) station.get("line")))) {
                                String tmpLine = (String) station.get("line");
                                for (Map<String, Object> translineData : transline) {
                                    if (first.equals((String) translineData.get("current")) && next.equals((String) translineData.get("next"))) {
                                        if (tmpLine.equals((String) translineData.get("transline"))) {
                                            String times = (String) translineData.get("time");
                                            String[] parts = times.split(":");
                                            int convertTime = convertToSeconds(parts[0], parts[1]);
                                            String fromLine = baseLine;
                                            String toLine = tmpLine;
                                            transferStations.add(new TransferInfo(first, next, fromLine, toLine, convertTime));
                                            flag=1;
                                            break;
                                        }
                                    }
                                }
                                baseLine = tmpLine;
                                if(flag==1)
                                    break;
                            }
                        }
                    }
                    if(flag == 1)
                        break;
                } // line 반복문
                if(flag == 1)
                    break;
            } // lines 반복문
        }
        return transferStations;
    }


    public static int calculatePathDistance(List<String> path)
    {

        //총 거리 구하기
        int distance = 0;
        String base_line = null;

        for (int i = 0; i < path.size() - 1; i++) {

            String first = path.get(i);
            String next = path.get(i + 1);

            Integer edgeWeight = subwayGraph.get(first).get(next);

            for (List<Map<String, Object>> line : lines) {
                int flag = 0;
                for (Map<String, Object> station : line) {
                    if (first.equals((String) station.get("name"))) {

                        if (station.get("next") instanceof List)
                        {
                            List<?> list = (List<?>) station.get("next");
                            for (int z = 0; z < list.size(); z++) {

                                String newer = (String) list.get(z);
                                String[] split_data = newer.split(",");
                                String tmp_next = split_data[0];


                                if (next.equals(tmp_next) && i == 0) {
                                    base_line = (String) station.get("line");
                                    flag = 1;
                                    break;
                                } else if (next.equals(tmp_next) && (base_line.equals((String) station.get("line"))))
                                {
                                    flag = 1;
                                    break;
                                }
                                else if (next.equals(tmp_next) && !(base_line.equals((String) station.get("line")))) {
                                    String tmp_line = (String) station.get("line");

                                    for (Map<String, Object> transline_data : transline)
                                    {
                                        if(first.equals((String) transline_data.get("current")) && next.equals((String) transline_data.get("next")))
                                        {
                                            if(tmp_line.equals((String) transline_data.get("transline")))
                                            {
                                                String times = (String) transline_data.get("time");
                                                String[] parts = times.split(":");
                                                int convert_time = convertToSeconds(parts[0], parts[1]);
                                                //System.out.println("추가된 시간: "+convert_time);
                                                distance += convert_time;
                                                flag = 1;
                                                break;
                                            }
                                        }
                                    }
                                    base_line = tmp_line;

                                }
                                if (flag == 1)
                                    break;

                            }
                        }
                        else
                        {
                            String newer = (String) station.get("next");
                            String[] split_data = newer.split(",");
                            String tmp_next = split_data[0];

                            if (next.equals(tmp_next) && i == 0) {
                                base_line = (String) station.get("line");
                                flag = 1;
                                break;
                            } else if (next.equals(tmp_next) && (base_line.equals((String) station.get("line"))))
                            {
                                flag = 1;
                                break;
                            }
                            else if (next.equals(tmp_next) && !(base_line.equals((String) station.get("line")))) {
                                String tmp_line = (String) station.get("line");

                                for (Map<String, Object> transline_data : transline)
                                {
                                    if(first.equals((String) transline_data.get("current")) && next.equals((String) transline_data.get("next")))
                                    {
                                        if(tmp_line.equals((String) transline_data.get("transline")))
                                        {
                                            String times = (String) transline_data.get("time");
                                            String[] parts = times.split(":");
                                            int convert_time = convertToSeconds(parts[0], parts[1]);
                                            //System.out.println("추가된 시간: "+convert_time);
                                            distance += convert_time;
                                            flag = 1;
                                            break;
                                        }
                                    }
                                }
                                base_line = tmp_line;
                                if(flag == 1)
                                    break;





                            }
                        }

                        if (station.get("prev") instanceof List) {
                            List<?> list = (List<?>) station.get("prev");
                            for (int z = 0; z < list.size(); z++) {

                                String newer = (String) list.get(z);
                                String[] split_data = newer.split(",");
                                String tmp_prev = split_data[0];

                                if (next.equals(tmp_prev) && i == 0) {
                                    base_line = (String) station.get("line");
                                    flag = 1;
                                    break;
                                } else if (next.equals(tmp_prev) && (base_line.equals((String) station.get("line"))))
                                {
                                    flag = 1;
                                    break;
                                }
                                else if (next.equals(tmp_prev) && !(base_line.equals((String) station.get("line")))) {
                                    String tmp_line = (String) station.get("line");
                                    for (Map<String, Object> transline_data : transline)
                                    {
                                        if(first.equals((String) transline_data.get("current")) && next.equals((String) transline_data.get("next")))
                                        {
                                            if(tmp_line.equals((String) transline_data.get("transline")))
                                            {
                                                String times = (String) transline_data.get("time");
                                                String[] parts = times.split(":");
                                                int convert_time = convertToSeconds(parts[0], parts[1]);
                                                //System.out.println("추가된 시간: "+convert_time);
                                                distance += convert_time;
                                                flag = 1;
                                                break;
                                            }
                                        }
                                    }
                                    base_line = tmp_line;
                                    if(flag == 1)
                                        break;
                                }
                            }
                        } else {
                            String newer = (String) station.get("prev");
                            String[] split_data = newer.split(",");
                            String tmp_prev = split_data[0];

                            if (next.equals(tmp_prev) && i == 0) {
                                base_line = (String) station.get("line");
                                flag = 1;
                                break;
                            } else if (next.equals(tmp_prev) && (base_line.equals((String) station.get("line"))))
                            {
                                flag = 1;
                                break;
                            }
                            else if (next.equals(tmp_prev) && !(base_line.equals((String) station.get("line")))) {
                                String tmp_line = (String) station.get("line");
                                for (Map<String, Object> transline_data : transline)
                                {
                                    if(first.equals((String) transline_data.get("current")) && next.equals((String) transline_data.get("next")))
                                    {
                                        if(tmp_line.equals((String) transline_data.get("transline")))
                                        {
                                            String times = (String) transline_data.get("time");
                                            String[] parts = times.split(":");
                                            int convert_time = convertToSeconds(parts[0], parts[1]);
                                            //System.out.println("추가된 시간: "+convert_time);
                                            distance += convert_time;
                                            flag = 1;
                                            break;
                                        }
                                    }
                                }
                                base_line = tmp_line;
                                if(flag == 1)
                                    break;
                            }
                        }
                    }

                    if(flag == 1)
                        break;
                }

                if(flag == 1)
                    break;
            }
            if (edgeWeight == null) {

                return Integer.MAX_VALUE;
            }
            distance += edgeWeight;
        }
        return distance;
    }

}
