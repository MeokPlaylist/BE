package com.meokplaylist;
import com.meokplaylist.api.dto.tourapi.PetTourApiProps;
import com.meokplaylist.api.dto.tourapi.TourApiProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({TourApiProps.class, PetTourApiProps.class})
public class MeokPlaylistApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeokPlaylistApplication.class, args);
    }

}
