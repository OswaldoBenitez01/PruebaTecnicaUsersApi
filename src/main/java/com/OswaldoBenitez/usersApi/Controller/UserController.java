package com.OswaldoBenitez.usersApi.Controller;

import com.OswaldoBenitez.usersApi.Model.User;
import com.OswaldoBenitez.usersApi.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/users")
@Tag(
        name = "Users",
        description = "Endpoints para gestionar usuarios (listado, filtrado, creación, actualización y eliminación). " +
                "Todos los endpoints requieren un JWT válido en el header Authorization: Bearer {token}."
)
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Listar usuarios",
            description = "Devuelve la lista de usuarios. " +
                    "Si se envía el parámetro 'filter', se aplica un filtro; " +
                    "en caso contrario, se puede ordenar con 'sortedBy'."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de filtro u ordenamiento inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No se envió un token JWT válido",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @Parameter(
                    description = "Campo por el cual ordenar la lista de usuarios. " +
                            "Valores permitidos: name, email, id, phone, tax_id, created_at. " +
                            "Si se omite, se devuelve la lista sin ordenar."
            )
            @RequestParam(required = false) String sortedBy,

            @Parameter(
                    description = "Filtro a aplicar con el formato: field operator value. " +
                            "Ejemplo: 'email sw user' o 'name co kev'. " +
                            "Operadores permitidos: co (contains), eq (equals), sw (starts with), ew (ends with). " +
                            "Si se envía este parámetro, se ignora 'sortedBy'."
            )
            @RequestParam(required = false) String filter) {

        if (filter != null) {
            List<User> filteredUsers = userService.getFilteredUsers(filter);
            return ResponseEntity.ok(filteredUsers);
        }

        List<User> userList = userService.getUsers(sortedBy);
        return ResponseEntity.ok(userList);
    }

    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario. " +
                    "Valida que name, email, phone, tax_id y password sean obligatorios. " +
                    "El RFC (tax_id) y el teléfono deben cumplir con el formato esperado; " +
                    "el password se encripta con AES-256 antes de guardarse."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado correctamente",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o campos obligatorios faltantes",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El tax_id ya existe en el sistema",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No se envió un token JWT válido",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(
                    description = "Objeto User con los datos del nuevo usuario. " +
                            "Campos obligatorios: name, email, phone, tax_id, password."
            )
            @RequestBody User newUser) {

        User createdUser = userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza parcialmente los datos de un usuario existente. " +
                    "Solo se modifican los campos enviados en el body. " +
                    "Valida formato de phone y tax_id, así como duplicidad de tax_id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos (phone o tax_id con formato incorrecto)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado para el id especificado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El tax_id ya está asignado a otro usuario",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No se envió un token JWT válido",
                    content = @Content
            )
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @Parameter(
                    description = "Identificador del usuario a actualizar (UUID en formato String)."
            )
            @PathVariable String userId,

            @Parameter(
                    description = "Objeto User con los campos a actualizar. " +
                            "Todos los campos son opcionales; solo se modifican los que se envíen."
            )
            @RequestBody User updatedFields) {

        User updatedUser = userService.updateUser(userId, updatedFields);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario existente por su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado correctamente (sin contenido en la respuesta)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado para el id especificado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No se envió un token JWT válido",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                    description = "Identificador del usuario a eliminar (UUID en formato String)."
            )
            @PathVariable String userId) {

        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
