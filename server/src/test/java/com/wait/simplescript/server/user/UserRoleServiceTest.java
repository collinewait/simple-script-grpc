package com.wait.simplescript.server.user;

import com.wait.simplescript.server.user.role.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UserRoleServiceTest {
    private UserRoleRepository repositoryMock;
    UserRoleService service;

    @BeforeEach
    public void setUp() {
        repositoryMock = mock(UserRoleRepository.class);
        service = new UserRoleServiceImpl(repositoryMock);
    }

    @Nested
    class GetUserRoles {
        @Test
        public void givenStringSetOfUserRole_thenReturnUserRole() {
            UserRole mockUserRole = new UserRole(ERole.USER);
            mockUserRole.setId("roleId");

            doReturn(Optional.of(mockUserRole)).when(repositoryMock).findByName(any(ERole.class));

            Set<UserRole> roles = service
                    .getUserRoles(new HashSet<>(Collections.singletonList("user")));
            assertThat(roles).extracting(UserRole::getName).contains(ERole.USER);
            assertEquals(1, roles.size());
        }

        @Test
        public void givenStringSetOfAdminRole_thenReturnAdminRole() {
            UserRole mockUserRole = new UserRole(ERole.ADMIN);
            mockUserRole.setId("roleId");

            doReturn(Optional.of(mockUserRole)).when(repositoryMock).findByName(any(ERole.class));

            Set<UserRole> roles = service
                    .getUserRoles(new HashSet<>(Collections.singletonList("admin")));
            assertThat(roles).extracting(UserRole::getName).contains(ERole.ADMIN);
            assertEquals(1, roles.size());
        }

        @Test
        public void givenStringSetOfUserAndAdminRoles_thenReturnUserAndAdminRoles() {
            UserRole mockUserRole1 = new UserRole(ERole.USER);
            mockUserRole1.setId("roleId2");
            UserRole mockUserRole2 = new UserRole(ERole.ADMIN);
            mockUserRole2.setId("roleId2");

            doReturn(Optional.of(mockUserRole1)).when(repositoryMock).findByName(ERole.USER);
            doReturn(Optional.of(mockUserRole2)).when(repositoryMock).findByName(ERole.ADMIN);

            Set<UserRole> roles = service
                    .getUserRoles(new HashSet<>(Arrays.asList("user", "admin")));
            assertThat(roles).extracting(UserRole::getName).contains(ERole.ADMIN);
            assertThat(roles).extracting(UserRole::getName).contains(ERole.USER);
            assertEquals(2, roles.size());
        }
    }
}
