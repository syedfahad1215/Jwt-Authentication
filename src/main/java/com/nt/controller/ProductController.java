package com.nt.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	
	private record Product( Integer id,
							String name,
							Double price
				) {}
	
	private List<Product> products = new ArrayList<>(
			List.of(
					new Product(1,"I phone 16",79999.00),
					new Product(2,"S 24 Ultra",89999.00),
					new Product(3,"Watch pro",39999.00))
			);

	
	@GetMapping
	public List<Product> getProducts(){
		return this.products;
	}
	
	@PostMapping
	public Product addProduct(@RequestBody Product product) {
		this.products.add(product);
		return product;
	}
	
	@GetMapping("/find")
	public Product findProduct(@RequestParam Integer id) {
		
		return this.products.get(--id);
	}
}
