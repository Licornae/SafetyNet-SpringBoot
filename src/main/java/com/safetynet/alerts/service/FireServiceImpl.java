package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FireDTO;
import com.safetynet.alerts.dto.PersonMedicalInfoDTO;
import com.safetynet.alerts.exception.AddressNotFoundException;
import com.safetynet.alerts.exception.DataNotLoadedException;
import com.safetynet.alerts.exception.ResidentsNotFoundException;
import com.safetynet.alerts.model.DataContainer;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.DataRepository;
import com.safetynet.alerts.util.AddressNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.safetynet.alerts.util.AddressNormalizer.equalsNormalized;


@Slf4j
@Service
@RequiredArgsConstructor
public class FireServiceImpl implements FireService {

    private final DataRepository dataRepository;
    private final MedicalInfoService medicalInfoService;

    /**
     * {@inheritDoc}
     */
    @Override
    public FireDTO getResidentsByAddress(String address) {

        String normAddress = AddressNormalizer.normalize(address);

        if (normAddress == null || normAddress.isBlank()) {
            log.error("getResidentsByAddress called with blank address (raw='{}')", address);
            throw new IllegalArgumentException("address must not be blank");
        }

        DataContainer dataContainer = dataRepository.getDataContainer();

        if (dataContainer == null) {
            log.error("DataContainer not loaded");
            throw new DataNotLoadedException("DataContainer not loaded");
        }

        //Find covering station using normalized address comparison
        FireStation fireStation = dataContainer.getFirestations().stream()
                .filter(fs -> fs != null && equalsNormalized(fs.getAddress(), normAddress))
                .findFirst()
                .orElse(null);

        if (fireStation == null) {
            log.error("getResidentsByAddress('{}'): no station found (normalized='{}')", address, normAddress);
            throw new AddressNotFoundException("No station for address " + address);
        }

        //Collect residents living at the address
        List<Person> personsAtAddress = dataContainer.getPersons().stream()
                .filter(person -> person != null && equalsNormalized(person.getAddress(), normAddress))
                .toList();

        if (personsAtAddress.isEmpty()) {
            log.error("getResidentsByAddress('{}'): station='{}' but no residents", address, fireStation.getStation());
            throw new ResidentsNotFoundException("No residents at address " + address);
        }

        //Build PersonMedicalInfoDTO, enriching each person with medical info
        List<PersonMedicalInfoDTO> residents = new ArrayList<>();
        for (Person person : personsAtAddress) {
            Integer age = medicalInfoService.getAgeFor(person);
            List<String> medications = medicalInfoService.getMedicationsFor(person);
            List<String> allergies = medicalInfoService.getAllergiesFor(person);

            residents.add(new PersonMedicalInfoDTO(
                    person.getFirstName(),
                    person.getLastName(),
                    person.getPhone(),
                    age,
                    medications,
                    allergies
            ));
        }
        log.debug("getResidentsByAddress('{}'): station='{}', residents={}", address, fireStation.getStation(), residents.size());

        return new FireDTO(fireStation.getStation(),residents);
    }
}
