package com.abstractmedia.server;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication

public class ServerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		System.out.println("Started service");
		SpringApplication.run(ServerApplication.class, args);
	}
}


enum BEER_TYPE{
	DARK, LIGHT
}

@Entity
class Beer {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String name;
	private BEER_TYPE type;

	public Beer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Beer(Integer id, String name,BEER_TYPE type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public Beer(String name) {
		super();
		this.name = name;
	}
	
	

	public Beer(String name, BEER_TYPE type) {
		super();
		this.name = name;
		this.type = type;
	}

	public BEER_TYPE getType() {
		return type;
	}

	public void setType(BEER_TYPE type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Beer [id=" + id + ", name=" + name + ", type=" + type + "]";
	}

	
}

@RepositoryRestResource
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5000","http://10.8.8.55:5000"})
interface BeerRepository extends JpaRepository<Beer, Integer> {

}

@Component
class BeerCommandLineRunner implements CommandLineRunner {

	@Autowired
	private BeerRepository beerRepository;

	@Override
	public void run(String... args) throws Exception {

		Stream.of("Jelen", "Lav", "Badwaiser", "Niksicko", "Apatinsko", "Tuborg").forEach(name -> {
			if(name.endsWith("o"))
				beerRepository.save(new Beer(name,BEER_TYPE.LIGHT));
			else
				beerRepository.save(new Beer(name,BEER_TYPE.DARK));
		});

		beerRepository.findAll().forEach(System.out::println);
	}
}

@RestController
class BeerController {

	@Autowired
	BeerRepository repository;

	@GetMapping("list-all")
	Collection<Beer> listBeers() {
		return repository.findAll();
	}
	
	@GetMapping("get-light")
	Collection<Beer> getByType(){
		
		return repository.findAll().stream().filter(this :: isLight).
				collect(Collectors.toList());
	}
	
	
	private Boolean isLight(Beer beer) {
		return beer.getType() == BEER_TYPE.LIGHT;
	}
}


@Configuration
 class RepositoryConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Beer.class);
    }
}
