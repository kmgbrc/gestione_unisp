package it.unisp.controller;

import it.unisp.model.Membri;
import it.unisp.service.MembriService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membri")
@RequiredArgsConstructor
public class MembriController {
    private final MembriService membriService;

    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Membri>> getAllMembri() {
        return ResponseEntity.ok(membriService.getAllMembri());
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Membri> createMembro(@RequestBody Membri membro) {
        return ResponseEntity.ok(membriService.registraMembro(membro));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Membri> updateMembro(@PathVariable Long id, @RequestBody Membri membro) {
        return ResponseEntity.ok(membriService.updateMembro(id, membro));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> deleteMembro(@PathVariable Long id) {
        membriService.deleteMembro(id);
        return ResponseEntity.ok().build();
    }
}
