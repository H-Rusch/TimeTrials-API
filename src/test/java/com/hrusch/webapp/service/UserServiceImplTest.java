package com.hrusch.webapp.service;

import com.hrusch.webapp.error.exception.UserDoesNotExistException;
import com.hrusch.webapp.error.exception.UsernameAlreadyTakenException;
import com.hrusch.webapp.model.dto.UserDto;
import com.hrusch.webapp.model.entity.UserEntity;
import com.hrusch.webapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static com.hrusch.webapp.UserUtil.createDto;
import static com.hrusch.webapp.UserUtil.createEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final UUID uuid = UUID.randomUUID();

    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder encoder;
    @Spy
    ModelMapper modelMapper;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
    }

    @Test
    void convertToUserDto_whenGivenEntity_convertToToCorrectDto() {
        UserEntity entity = createEntity(uuid);

        UserDto dto = userService.convertToUserDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getUserId(), dto.getUserId());
        assertEquals(entity.getUsername(), dto.getUsername());
        assertEquals(entity.getEncryptedPassword(), dto.getEncryptedPassword());
    }

    @Test
    void convertToUserEntity_whenGivenDto_convertToToCorrectEntity() {
        UserDto dto = createDto(uuid);
        String encryptedPassword = "encrypted_password_123";
        when(encoder.encode(anyString())).thenReturn(encryptedPassword);

        UserEntity entity = userService.convertToUserEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getUserId(), entity.getUserId());
        assertEquals(dto.getUsername(), entity.getUsername());
        assertEquals(encryptedPassword, entity.getEncryptedPassword());
    }

    @Test
    void creatingUserReturnsUserObject() throws UsernameAlreadyTakenException {
        when(userRepository.save(any(UserEntity.class))).thenReturn(createEntity(uuid));
        var dto = createDto(uuid);

        var result = userService.createUser(dto);

        assertEquals(dto.getUserId(), result.getUserId());
        assertEquals(dto.getUsername(), result.getUsername());
    }

    @Test
    void creatingUserWithNonUniqueNameThrowsException() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(createEntity(uuid)));
        var dto = createDto(uuid);

        var result = assertThrows(UsernameAlreadyTakenException.class, () -> userService.createUser(dto));
        assertEquals(String.format("The username has already been taken: %s", dto.getUsername()), result.getMessage());
    }

    @Test
    void findUserByUserId_whenUserFoundInDatabase_userIsReturnedCorrectly() throws UserDoesNotExistException {
        when(userRepository.findByUserId(any(String.class))).thenReturn(Optional.of(createEntity(uuid)));

        var result = userService.findUserByUserId(String.valueOf(uuid));

        assertThat(result.getUserId()).isEqualTo(uuid.toString());
    }

    @Test
    void findUserByUserId_whenUserNotFoundInDatabase_exceptionIsThrown() {
        when(userRepository.findByUserId(any(String.class))).thenReturn(Optional.empty());

        var result = assertThrows(UserDoesNotExistException.class,
                () -> userService.findUserByUserId(String.valueOf(uuid)));
        assertThat(result.getMessage()).endsWith("does not exist.");
    }
}
