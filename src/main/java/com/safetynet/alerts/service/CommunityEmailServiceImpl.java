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

/**
 * Default implementation of CommunityEmailService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityEmailServiceImpl implements CommunityEmailService {

    private final DataRepository dataRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailDTO> getEmailsByCity(String city) {
        log.info("communityEmail.getEmailsByCity called with city='{}'", city);

        if (city == null || city.isBlank()) {
            log.warn("communityEmail: blank or null city parameter");
            throw new IllegalArgumentException("city must not be blank");
        }

        boolean cityExists = dataRepository.getDataContainer().getPersons().stream()
                .filter(Objects::nonNull)
                .map(Person::getCity)
                .anyMatch(c -> c != null && c.equals(city));

        if (!cityExists) {
            log.warn("communityEmail: city not found '{}'", city);
            throw new CityNotFoundException("Unknown city");
        }

        List<String> emails = dataRepository.getDataContainer().getPersons().stream()
                .filter(Objects::nonNull)
                .filter(person -> person.getCity().equals(city))
                .map(Person::getEmail)
                .toList();

        List<EmailDTO> distinctEmail = emails.stream()
                .distinct()
                .map(EmailDTO::new)
                .toList();
        log.info("communityEmail: returning {} distinct email(s) for city='{}'", distinctEmail.size(), city);

        return distinctEmail;
    }
}
