package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        int singleScreenPrice = 200;
        int intialPrice = 500;
        if(subscriptionEntryDto.getSubscriptionType()==SubscriptionType.PRO){
            singleScreenPrice = 250;
            intialPrice = 800;
        }
        else if(subscriptionEntryDto.getSubscriptionType()==SubscriptionType.ELITE){
            singleScreenPrice = 350;
            intialPrice = 1000;
        }

        Date currentDate = new Date();
        int amountToBePaid = intialPrice + (subscriptionEntryDto.getNoOfScreensRequired()*singleScreenPrice);

        Subscription subscription = new Subscription(subscriptionEntryDto.getSubscriptionType(),
                subscriptionEntryDto.getNoOfScreensRequired(),
                currentDate,
                amountToBePaid);

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        user.setSubscription(subscription);
        subscription.setUser(user);

        userRepository.save(user);

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType()==SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }

        int currentPlanBill = user.getSubscription().getTotalAmountPaid();
        int noOfScreen = user.getSubscription().getNoOfScreensSubscribed();
        int updatePlanBill = 0;
        if(user.getSubscription().getSubscriptionType()==SubscriptionType.BASIC){
            updatePlanBill = 800 + 250*noOfScreen;
            Subscription currentSubscription = user.getSubscription();
            currentSubscription.setSubscriptionType(SubscriptionType.PRO);
            user.setSubscription(currentSubscription);
        }
        else if(user.getSubscription().getSubscriptionType()==SubscriptionType.PRO){
            updatePlanBill = 1000 + 350*noOfScreen;
            Subscription currentSubscription = user.getSubscription();
            currentSubscription.setSubscriptionType(SubscriptionType.ELITE);
            user.setSubscription(currentSubscription);
        }
        userRepository.save(user);
        int differ = updatePlanBill - currentPlanBill;
        return differ;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        Integer totalRevenue = 0;
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        for(Subscription subscription:subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
