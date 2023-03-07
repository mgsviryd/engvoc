package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.VerificationToken;
import by.sviryd.engvoc.repos.VerificationTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepo verificationTokenRepo;

    @Autowired
    public VerificationTokenService(VerificationTokenRepo verificationTokenRepo) {
        this.verificationTokenRepo = verificationTokenRepo;
    }

    public void save(VerificationToken token) {
        verificationTokenRepo.save(token);
    }

    public Optional<VerificationToken> findById(String id) {
        return verificationTokenRepo.findById(id);
    }

    public void deleteById(String id) {
        verificationTokenRepo.deleteById(id);
    }
    public void delete(VerificationToken token) {
        verificationTokenRepo.delete(token);
    }

}
