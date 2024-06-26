package edu.tienda.core.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.tienda.core.domain.Cliente;
import edu.tienda.core.exceptions.ResourceNotFoundException;

@RestController
@RequestMapping("/clientes")
public class ClienteRestController {

    private List<Cliente> clientes = new ArrayList<>(Arrays.asList(
            new Cliente("arm", "1234", "Armstrong"),
            new Cliente("ald", "1234", "Aldrin"),
            new Cliente("col", "1234", "collins")));

    @GetMapping
    public ResponseEntity<?> getClientes() {
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getCliente(@PathVariable String username) throws BadRequestException {

        
            if (username.length()!=3) {
                throw new BadRequestException("El Parámetro nombre debe contener 3 caracteres");  
            }
    
            return clientes.stream()
            .filter(cliente -> cliente.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .map(ResponseEntity :: ok)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente: " + username + " no encontrado"));
       
    }

    @PostMapping
    public ResponseEntity<?> altaCliente(@RequestBody Cliente cliente) {
        clientes.add(cliente);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(cliente.getUsername())
                .toUri();
        return ResponseEntity.created(location).body(cliente);
    }

    @PutMapping
    public ResponseEntity<?> modificacionCliente(@RequestBody Cliente cliente) {
        Cliente clienteEncontrado = clientes.stream()
                .filter(cli -> cli.getUsername().equalsIgnoreCase(cliente.getUsername())).findFirst().orElseThrow();

        clienteEncontrado.setPassword(cliente.getPassword());
        clienteEncontrado.setNombre(cliente.getNombre());

        return ResponseEntity.ok(clienteEncontrado);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteCliente(@PathVariable String username) {

        Cliente clienteEncontrado = clientes.stream().filter(cli -> cli.getUsername().equalsIgnoreCase(username))
                .findFirst().orElseThrow();

        clientes.remove(clienteEncontrado);
        return ResponseEntity.noContent().build();
    }
}
