package com.javadevjournal.service;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.repository.CustomerRepository;
import com.javadevjournal.security.MyResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	private final ApartmentService apartmentService;
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public String login(String username, String password) {
		Optional<Customer> customer = customerRepository.login(username, password);
		if (customer.isPresent()) {
			Base64.Encoder encoder = Base64.getEncoder();
			String token = new String(encoder.encode(username.getBytes())) +
					":" + new String(encoder.encode(password.getBytes()));
			Customer custom = customer.get();
			custom.setToken(token);
			customerRepository.save(custom);
			return token;
		}

		return "";
	}

	@Override
	public List<Customer> findAll() {
		return (List<Customer>) customerRepository.findAll();
	}

	@Override
	public Optional<User> findByToken(String token) {
		Optional<Customer> customer = customerRepository.findByToken(token);
		if (customer.isPresent()) {
			Customer customer1 = customer.get();
			User user = new User(customer1.getUserName(), customer1.getPassword(), true, true, true, true,
					AuthorityUtils.createAuthorityList("USER"));
			return Optional.of(user);
		}
		return Optional.empty();
	}

	@Override
	public Customer findById(Long id) {
		Optional<Customer> customer = customerRepository.findById(id);
		return customer.orElseThrow(() -> new MyResourceNotFoundException("Пользователь не найден"));
	}

	@Override
	public Customer registration(CustomerDTO customerDTO) {
		Customer customer = new Customer();
		if (customerDTO.getName() == null || customerDTO.getPassword() == null) {
			throw new MyResourceNotFoundException("Вы ошиблись, отсутствует имя или пароль");
		}
		customer.setUserName(customerDTO.getName());
		customer.setPassword(customerDTO.getPassword());
		var res = customerRepository.findByUserNameAndPassword(
				customerDTO.getName(),
				customerDTO.getPassword()
		);
		return res.orElseGet(() -> customerRepository.save(customer));
	}

	@Override
	public Optional<Customer> whoIs(HttpServletRequest httpServletRequest) {
		String token = !Objects.equals(httpServletRequest.getHeader(AUTHORIZATION), "") ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		token = token.replaceAll("Basic", "").trim();
		return customerRepository.findByToken(token);
	}

	@Override
	public Optional<Customer> whoIs(String userToken) {
		String token = !Objects.equals(userToken, "") ? userToken : "";
		token = token.replaceAll("Basic", "").trim();
		return customerRepository.findByToken(token);
	}

	@Override
	@Transactional
	public void deleteMe(HttpServletRequest httpServletRequest) {
		var customerOpt = whoIs(httpServletRequest);
		if (!customerOpt.isPresent()) {
			throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
		}
		var customer = customerOpt.get();
		apartmentService.deleteAllByOwner(customer);
		apartmentService.unApprove(customer);
		customerRepository.delete(customer);
	}

	@Override
	@Transactional
	public String complaint(HttpServletRequest httpServletRequest, Long customerId) {
		var customerOpt = whoIs(httpServletRequest);
		if (!customerOpt.isPresent()) {
			throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
		}
		var customer = customerOpt.get();
		if (customer.isBanned()) {
			throw new MyResourceNotFoundException("Пользователя на которого вы жалуетесь уже забанен");
		}
		Customer anotherUser = findById(customerId);
		if (anotherUser == null) {
			throw new MyResourceNotFoundException("Пользователя на которого вы жалуетесь не существует");
		}
		if (anotherUser.isBanned()) {
			throw new MyResourceNotFoundException("Пользователя на которого вы жалуетесь уже забанен");
		}
		return complaint(anotherUser);
	}

	@Override
	public void save(Customer customer) {
		customerRepository.save(customer);
	}

	private String complaint(Customer customer) {
		customer.incNegative();
		if (customer.getKarmaNegative() >= 5) {
			customer.setBanned(true);
			customerRepository.save(customer);
			apartmentService.deleteAllByOwner(customer);
			apartmentService.unApprove(customer);
			return "Пользователь успешно забанен!";
		} else {
			customerRepository.save(customer);
			return "Жалоба успешно отправлена!";
		}
	}
}
