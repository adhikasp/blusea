package com.blibli.future.utility;

import com.blibli.future.model.Catering;
import com.blibli.future.model.Customer;
import com.blibli.future.model.User;
import com.blibli.future.repository.UserRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Helper {
    @Autowired
    UserRepository userRepository;
    @Autowired
    protected AuthenticationManager authenticationManager;

    public boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken) ;
    }

    public String getEmail() {
        return getCurrentUser().getEmail();
    }

    public boolean isLoggedInAsCustomer() {
        if (isLoggedIn()) {
            User u = getCurrentUser();
            return u instanceof Customer;
        }
        return false;
    }

    public boolean isLoggedInAsCatering() {
        if (isLoggedIn()) {
            User u = getCurrentUser();
            return u instanceof Catering;
        }
        return false;
    }

    public User getCurrentUser() {
        if (isLoggedIn()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return userRepository.findByUsername(auth.getName());
        }
        return null;
    }

    public ArrayList<Pair<Integer, Integer>> getPricePair(String priceData) {
        ArrayList<Pair<Integer, Integer>> pricePair = new ArrayList<>();
        for (String stringPair: priceData.split("\\|")) {
            int minQuantity = Integer.parseInt(stringPair.split("\\-")[0]);
            int price = Integer.parseInt(stringPair.split("\\-")[1]);
            pricePair.add(Pair.of(minQuantity, price));
        }
        return pricePair;
    }

    public String setProductPrice(String[] quantity, String[] price) {
        String productPrice = "";
        int priceSize = quantity.length;
        for(int i=0;i<priceSize;i++){
            String productPriceTmp;
            if(i!=priceSize-1) productPriceTmp = quantity[i] + "-" + price[i] + "|";
            else productPriceTmp = quantity[i] + "-" + price[i];
            productPrice = productPrice + productPriceTmp;
        }
        return productPrice;
    }

    public Customer getCurrentCustomer() {
        return (Customer) getCurrentUser();
    }

    public Catering getCurrentCatering() {
        return (Catering) getCurrentUser();
    }

    public void authenticateUserAndSetSession(User user, HttpServletRequest request) {
        String username = user.getUsername();
        String password = user.getPassword();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        // generate session if one doesn't exist
        request.getSession();

        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
    }
}
