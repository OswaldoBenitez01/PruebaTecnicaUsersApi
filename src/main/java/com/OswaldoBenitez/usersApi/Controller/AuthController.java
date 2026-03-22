package com.OswaldoBenitez.usersApi.Controller;

import com.OswaldoBenitez.usersApi.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@Tag(
        name = "Auth",
        description = "Endpoint público para autenticación. No requiere token. " +
                "Devuelve un JWT que se debe enviar en el header " +
                "Authorization: Bearer {token} para consumir los endpoints protegidos."
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica a un usuario usando taxId y password. " +
                    "Si las credenciales son correctas, devuelve un token JWT en texto plano. " +
                    "Este token debe utilizarse en el header Authorization: Bearer {token} " +
                    "para acceder a los endpoints de /users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa. Se devuelve el token JWT.",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas (taxId o password incorrectos).",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping
    public ResponseEntity<String> login(
            @RequestBody(
                    description = "Parámetros de autenticación enviados como form-data o query params. " +
                            "El taxId debe existir previamente y el password debe coincidir " +
                            "con el almacenado (se valida desencriptando la contraseña guardada).",
                    required = false,
                    content = @Content
            )
            @RequestParam String taxId,
            @RequestParam String password) {

        try {
            String token = authService.login(taxId, password);
            return ResponseEntity.ok(token);
        } catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
    }
}
