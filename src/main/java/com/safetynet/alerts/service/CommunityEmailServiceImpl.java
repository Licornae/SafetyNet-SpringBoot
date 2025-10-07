package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.EmailDTO;
import com.safetynet.alerts.exception.CityNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityEmailServiceImpl implements CommunityEmailService {

    private final DataRepository dataRepository;

    @Override
    public List<EmailDTO> getEmailsByCity(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city must not be blank");
        }

        boolean cityExists = dataRepository.getDataContainer().getPersons().stream()
                .filter(Objects::nonNull)
                .map(Person::getCity)
                .anyMatch(c -> c != null && c.equals(city));

        if (!cityExists) {
            throw new CityNotFoundException("Unknown city");
        }

        List<String> emails = dataRepository.getDataContainer().getPersons().stream()
                .filter(Objects::nonNull)
                .filter(person -> person.getCity().equals(city))
                .map(Person::getEmail)
                .toList();

        return emails.stream()
                .distinct()
                .map(EmailDTO::new)
                .toList();
    }
}
