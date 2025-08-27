package com.sopo.controller.api.address;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.address.request.AddressCreateRequest;
import com.sopo.dto.address.request.AddressUpdateRequest;
import com.sopo.dto.address.response.AddressResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.address.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressApiController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginMember MemberSession session,
                                       @Valid @RequestBody AddressCreateRequest req) {
        Long id = addressService.add(session.id(), req);

        return ResponseEntity.created(URI.create("/api/addresses/" + id)).build();
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> list(@LoginMember MemberSession session) {
        List<AddressResponse> responses = addressService.list(session.id());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> get(@LoginMember MemberSession session,
                                               @PathVariable("addressId") Long addressId) {
        AddressResponse response = addressService.get(session.id(), addressId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{addressId}")
    public ResponseEntity<Void> update(@LoginMember MemberSession session,
                                       @PathVariable("addressId") Long addressId,
                                       @Valid @RequestBody AddressUpdateRequest req) {
        addressService.update(session.id(), addressId, req);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefault(@LoginMember MemberSession session,
                                           @PathVariable("addressId") Long addressId) {
        addressService.setDefault(session.id(), addressId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@LoginMember MemberSession session,
                                       @PathVariable("addressId") Long addressId) {
        addressService.remove(session.id(), addressId);

        return ResponseEntity.noContent().build();
    }
}