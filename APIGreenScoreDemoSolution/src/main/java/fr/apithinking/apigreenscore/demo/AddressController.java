package fr.apithinking.apigreenscore.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Annotation NON_NULL pour ne pas générer les champs à null
@JsonInclude(JsonInclude.Include.NON_NULL)
record Address(String street, String city) {
}


@RestController
@RequestMapping("/addresses")
@Slf4j
public class AddressController {

    private static final List<Address> addresses = List.of(
            new Address("10 avenue des Champs-Elysées", "Paris"),
            new Address("15 rue des Plantes", "Nantes"),
            new Address("5 rue de la Paix", "Lyon"),
            new Address("12 boulevard Haussmann", "Marseille"),
            new Address("50 rue Nationale", "Bordeaux"),
            new Address("20 avenue des Champs-Elysées", "Paris"),
            new Address("25 rue des Plantes", "Nantes"),
            new Address("10 rue de la Paix", "Lyon"),
            new Address("22 boulevard Haussmann", "Marseille"),
            new Address("60 rue Nationale", "Bordeaux"),
            new Address("30 avenue des Champs-Elysées", "Paris"),
            new Address("35 rue des Plantes", "Nantes"),
            new Address("15 rue de la Paix", "Lyon"),
            new Address("32 boulevard Haussmann", "Marseille"),
            new Address("70 rue Nationale", "Bordeaux"),
            new Address("40 avenue des Champs-Elysées", "Paris"),
            new Address("45 rue des Plantes", "Nantes"),
            new Address("20 rue de la Paix", "Lyon"),
            new Address("42 boulevard Haussmann", "Marseille"),
            new Address("80 rue Nationale", "Bordeaux"),
            new Address("50 avenue des Champs-Elysées", "Paris"),
            new Address("55 rue des Plantes", "Nantes"),
            new Address("25 rue de la Paix", "Lyon"),
            new Address("52 boulevard Haussmann", "Marseille"),
            new Address("90 rue Nationale", "Bordeaux"),
            new Address("60 avenue des Champs-Elysées", "Paris"),
            new Address("65 rue des Plantes", "Nantes"),
            new Address("30 rue de la Paix", "Lyon"),
            new Address("62 boulevard Haussmann", "Marseille"),
            new Address("100 rue Nationale", "Bordeaux"),
            new Address("70 avenue des Champs-Elysées", "Paris"),
            new Address("75 rue des Plantes", "Nantes"),
            new Address("35 rue de la Paix", "Lyon"),
            new Address("72 boulevard Haussmann", "Marseille"),
            new Address("110 rue Nationale", "Bordeaux"),
            new Address("80 avenue des Champs-Elysées", "Paris"),
            new Address("85 rue des Plantes", "Nantes"),
            new Address("40 rue de la Paix", "Lyon"),
            new Address("82 boulevard Haussmann", "Marseille"),
            new Address("120 rue Nationale", "Bordeaux"),
            new Address("90 avenue des Champs-Elysées", "Paris"),
            new Address("95 rue des Plantes", "Nantes"),
            new Address("45 rue de la Paix", "Lyon"),
            new Address("92 boulevard Haussmann", "Marseille"),
            new Address("130 rue Nationale", "Bordeaux"),
            new Address("100 avenue des Champs-Elysées", "Paris"),
            new Address("105 rue des Plantes", "Nantes"),
            new Address("10  avenue du Genetay", "Nantua"),
            new Address("5 Chemin de Bretagne", "Issy Les Moulineaux"),
            new Address("ru de Bretagne", "Paimpont"),
            new Address("50 rue de la Paix", "Lyon"),
            new Address("102 boulevard Haussmann", "Marseille"),
            new Address("chemin des pecheurs", "Marseillan"),
            new Address("140 rue Nationale", "Bordeaux")
    );

    /**
     * Endpoint to get a list of addresses filtered by street and city.
     *
     * @param limitStr the maximum number of addresses to return
     * @param street the street name to filter by (optional)
     * @param city the city name to filter by (optional)
     * @param fields the fields to include in the response (optional)
     * @return a list of addresses matching the filters
     */
    @GetMapping(value = "/address")
    public ResponseEntity<List<Address>> getAddresses(
            @RequestParam(defaultValue = "2") String limitStr,
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String fields) {

        return ResponseEntity.ok()
                .body(addresses.stream()
                        .filter(address -> {
                            if (StringUtils.isEmpty(city)) {
                                return true;
                            }
                            return StringUtils.containsIgnoreCase(address.city(), city);
                        })
                        .filter(address -> {
                            if (StringUtils.isEmpty(street)) {
                                return true;
                            }
                            return StringUtils.containsIgnoreCase(address.street(), street);
                        })
                        .map(address -> {
                            // Pas de filtre, je renvoie tout
                            if (StringUtils.isEmpty(fields)) {
                                return address;
                            }

                            // Un filtre, je ne renvoie que les champs demandés
                            String streetOut = null;
                            String cityOut = null;
                            if (StringUtils.containsIgnoreCase(fields, "street")) {
                                streetOut = address.street();
                            }
                            if (StringUtils.containsIgnoreCase(fields, "city")) {
                                cityOut = address.city();
                            }

                            return new Address(streetOut, cityOut);
                        })
                        .limit(NumberUtils.toInt(limitStr))
                        .toList());
    }
}


