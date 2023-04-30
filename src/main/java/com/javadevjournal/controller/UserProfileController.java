package com.javadevjournal.controller;

import com.javadevjournal.dto.ApartmentDTO;
import com.javadevjournal.jpa.entity.Apartment;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Vote;
import com.javadevjournal.service.ApartmentService;
import com.javadevjournal.service.CustomerService;
import com.javadevjournal.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserProfileController {

    private final CustomerService customerService;
    private final ApartmentService apartmentService;
    private final VoteService voteService;

    @GetMapping(value = "/users/user/{id}", produces = "application/json")
    public Customer getUserDetail(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @GetMapping(value = "/users/user/all", produces = "application/json")
    public List<Customer> getAllUsers() {
        return customerService.findAll();
    }

    @GetMapping(value = "/users/complaint/{id}")
    public String complaint(HttpServletRequest httpServletRequest,
                            @PathVariable Long id) {
        return customerService.complaint(httpServletRequest, id);
    }

    @GetMapping(value = "/apartment/filter", produces = "application/json")
    public List<Apartment> findApartments(@RequestParam("minPrice") final Long minPrice,
                                          @RequestParam("maxPrice") final Long maxPrice,
                                          @RequestParam("floor") final Integer floor,
                                          @RequestParam("rooms") final Integer rooms) {
        return apartmentService.findApartmentsByFilter(minPrice, maxPrice, floor, rooms);
    }

    @GetMapping(value = "/apartments/my", produces = "application/json")
    public List<Apartment> findMyApartments(HttpServletRequest httpServletRequest) {
        var customerOpt = customerService.whoIs(httpServletRequest);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new RuntimeException("Вы забанены, у вас не может быть объявлений");
        }
        return apartmentService.findMyApartments(customer);
    }

    @GetMapping(value = "/apartments/all", produces = "application/json")
    public List<Apartment> findAllApprovedApartments() {
        return apartmentService.findAllApprovedApartments();
    }


    @PostMapping(value = "/apartment/create", produces = "application/json")
    public Apartment createApartment(HttpServletRequest httpServletRequest,
                                     @RequestBody ApartmentDTO apartmentDTO) {
        var customerOpt = customerService.whoIs(httpServletRequest);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new RuntimeException("Вы забанены, вам нельзя выставлять квартиры на продажу");
        }
        return apartmentService.createApartment(apartmentDTO, customer);
    }

    @PostMapping(value = "/apartment/vote/{id}")
    public String createOffer(HttpServletRequest httpServletRequest,
                              @PathVariable Long id,
                              @RequestParam("price") final String price) {
        var customerOpt = customerService.whoIs(httpServletRequest);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new RuntimeException("Вы забанены, вам нельзя участвовать в голосовании");
        }
        if (!customer.isProfessional()) {
            return "Вы не имеете права голосовать, вашего опыта еще не достаточно";
        }
        apartmentService.makeOffer(id, customer, Long.valueOf(price));
        return "Спасибо! Ваш голос учтен!";
    }

    @GetMapping(value = "/apartment/vote/list")
    public List<Vote> getAllOpenedVotes() {
        return voteService.getAllOpenedVotes();
    }

}
