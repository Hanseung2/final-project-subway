package com.example.member.controller;

import com.example.member.domain.InputData;
import com.example.member.domain.OutputData;
import com.example.member.dto.PathWithLength;
import com.example.member.dto.RequestRoute;
import com.example.member.dto.SearchRouteDTO;
import com.example.member.DemoApplication;
import com.example.member.service.FindRouteService;
import com.example.member.service.FindRouteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class SpringController {

    private final FindRouteService findRouteService;

    @Autowired
    public SpringController(FindRouteService routeService) {
        this.findRouteService = routeService;
    }


    @PostMapping("/SearchRoute")
    public List<OutputData> getShortestPaths(@RequestParam String start, @RequestParam String end, @RequestParam Integer time, @RequestParam String day) {
        return findRouteService.calculateTimeForPath(start,end,time,day);
    }

    @PostMapping("/Predict")
    public List<OutputData> getPredict(@RequestBody List<OutputData> outputData,@RequestParam String day){
        return findRouteService.calculateAI(outputData,day);
    }

}
