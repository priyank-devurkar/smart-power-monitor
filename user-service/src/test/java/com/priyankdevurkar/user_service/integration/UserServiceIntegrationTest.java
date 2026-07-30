package com.priyankdevurkar.user_service.integration;

import com.priyankdevurkar.user_service.dto.UserDto;
import com.priyankdevurkar.user_service.entity.User;
import com.priyankdevurkar.user_service.repository.UserRepository;
import com.priyankdevurkar.user_service.testsupport.MySqlTestcontainersBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public class UserServiceIntegrationTest extends MySqlTestcontainersBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_viaRestApi_persistsAndReturnsUser() {

        UserDto request = UserDto.builder()
                .name("Smart")
                .surname("PowerMonitor")
                .email("smartmonitor@gmail.com")
                .address("A104 Garden Lane")
                .alerting(true)
                .energyAlertingThreshold(2000.0)
                .build();

        ResponseEntity<UserDto> response =
                restTemplate.postForEntity("/api/v1/user", request, UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Smart");
        assertThat(response.getBody().getSurname()).isEqualTo("PowerMonitor");
        assertThat(response.getBody().getEmail()).isEqualTo("smartmonitor@gmail.com");
        assertThat(response.getBody().getAddress()).isEqualTo("A104 Garden Lane");
        assertThat(response.getBody().isAlerting()).isTrue();
        assertThat(response.getBody().getEnergyAlertingThreshold()).isEqualTo(2000.0);

        ResponseEntity<UserDto> loaded =
                restTemplate.getForEntity("/api/v1/user/"
                        + response.getBody().getId(), UserDto.class);

        assertThat(loaded.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loaded.getBody()).isNotNull();
        assertThat(loaded.getBody().getEmail()).isEqualTo("smartmonitor@gmail.com");

    }


}
