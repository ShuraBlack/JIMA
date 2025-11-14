package de.shurablack.jima;

import de.shurablack.jima.http.Requester;


public class Main {

    public static void main(String[] args) {
        var response = Requester.getCompanionExchangeListings(2);
        System.out.println();
    }

}
