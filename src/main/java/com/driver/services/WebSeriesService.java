package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public WebSeries findBySeriesName(String seriesName){
        WebSeries webSeries = webSeriesRepository.findBySeriesName(seriesName);
        return webSeries;
    }

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        WebSeries webSeries1 = findBySeriesName(webSeriesEntryDto.getSeriesName());
        if(webSeries1!=null){
            throw new Exception("Series is already present");
        }
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        double ratingSum=0;
        int webSeriesCount=0;
        for(WebSeries webSeries:webSeriesList){
            ratingSum=ratingSum+webSeries.getRating();
            webSeriesCount+=1;
        }
        double newRating = (ratingSum+webSeriesEntryDto.getRating())/(webSeriesCount+1);
        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(),webSeriesEntryDto.getAgeLimit(),
                webSeriesEntryDto.getRating(),webSeriesEntryDto.getSubscriptionType());
        productionHouse.setRatings(newRating);
        webSeries.setProductionHouse(productionHouse);
        productionHouse.getWebSeriesList().add(webSeries);
        WebSeries newWebSeriesAdded = webSeriesRepository.save(webSeries);
//        productionHouseRepository.save(productionHouse);
        return newWebSeriesAdded.getId();
    }

}
