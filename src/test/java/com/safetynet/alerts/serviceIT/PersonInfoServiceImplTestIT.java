package com.safetynet.alerts.serviceIT;

import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PersonInfoServiceImplTestIT {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    PersonInfoServiceImpl service;

    @BeforeEach
    public void reload() { dataRepository.reloadData(); }

    @Test
    public void testGetPersonInfoByLastName_shouldWorkWithRealDataset() {
        String lastName = "Boyd";

        List<PersonInfoDTO> list = personInfoService.getPersonsInfoByLastName(lastName);

        assertNotNull(list, "La liste ne doit pas être nulle");
        assertFalse(list.isEmpty(), "Le nom 'Boyd' devrait retourner des personnes");

        assertTrue(list.stream().allMatch(p -> lastName.equalsIgnoreCase(p.getLastName())),
                "Tous les DTO doivent porter le nom 'Boyd'");

        PersonInfoDTO first = list.get(0);
        assertNotNull(first.getAge(), "L'âge doit être calculé");
        assertNotNull(first.getEmail(), "L'email doit être présent");
        assertNotNull(first.getMedications(), "La liste des médicaments ne doit pas être nulle (peut être vide)");
        assertNotNull(first.getAllergies(), "La liste des allergies ne doit pas être nulle (peut être vide)");
    }


}
