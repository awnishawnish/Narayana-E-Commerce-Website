//package NarayanGroup.example.E_Commerce.controller;
//
//import NarayanGroup.example.E_Commerce.DTO.request.LoginDTO;
//import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
//import NarayanGroup.example.E_Commerce.controller.Impl.LoginController;
//import NarayanGroup.example.E_Commerce.facade.ILoginFacade;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.ResponseEntity;
//
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.mockito.Mockito.*;
//
//class LoginControllerTest {
//    private final ILoginFacade facade = mock(ILoginFacade.class);
//    private final LoginController controller = new LoginController(facade);
//
//    @Test void authenticateDelegatesToFacade() {
//        LoginDTO request = new LoginDTO();
//        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
//        ResponseEntity<ResponseMessageUtilityDTO> expected = ResponseEntity.ok(new ResponseMessageUtilityDTO());
//        when(facade.authenticateUser(request, servletResponse)).thenReturn(expected);
//        assertSame(expected, controller.authenticateUser(request, servletResponse));
//    }
//
//    @Test void refreshDelegatesToFacade() {
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        ResponseEntity<?> expected = ResponseEntity.ok().build();
//        when(facade.refreshToken(request)).thenReturn(expected);
//        assertSame(expected, controller.refreshToken(request));
//    }
//
//    @Test void logoutDelegatesToFacade() {
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        ResponseEntity<?> expected = ResponseEntity.ok().build();
//        when(facade.logout(response)).thenReturn(expected);
//        assertSame(expected, controller.logout(response));
//    }
//}
