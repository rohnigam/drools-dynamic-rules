package com.javatechie.spring.drools.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {

	private String name;
	private String cardType;
	private int discount;
	private int price;
	private String clientType;
	private String rulesFileProcessed;

}
