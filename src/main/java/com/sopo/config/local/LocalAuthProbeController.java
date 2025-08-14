package com.sopo.config.local;

import com.sopo.config.auth.LoginMember;
import com.sopo.security.session.MemberSession;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController
public class LocalAuthProbeController {

    @GetMapping("/api/_probe/me")
    public ResponseEntity<MemberSession> me(@LoginMember MemberSession me) {
        return ResponseEntity.ok(me);
    }
}
