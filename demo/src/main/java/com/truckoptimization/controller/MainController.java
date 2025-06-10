package com.truckoptimization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.truckoptimization.service.DistanceToLatLongConvertor;
import com.truckoptimization.service.OptimizeRoutes;

@Controller
public class MainController {

    @Autowired
    private OptimizeRoutes optimizeRoutes;

    @Autowired
    private DistanceToLatLongConvertor distanceToLatLongConvertor;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @PostMapping("/optimize")
    public String optimizeRoutes(@RequestParam("csvFile") MultipartFile file, String address, Integer numOfTrucks, Integer capacityOfTrucks, Model model){

        //check if user uploaded a file, otherwise

        String[] addressSplit = address.split("\\r?\\n");

        for(String a : addressSplit){
            distanceToLatLongConvertor.convertAddressToLatLong(a);
        }

        //add routes and other data to model
        return "/home";
    }
    
}
