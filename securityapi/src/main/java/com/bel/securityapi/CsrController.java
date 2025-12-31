package com.bel.securityapi;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/csr")
@CrossOrigin(origins = "*") // ✅ CORS FIX (allow frontend)
public class CsrController {

    private final CsrService csrService;
    private final CsrRepository csrRepository;
    private final CsrPdfService csrPdfService;

    public CsrController(
            CsrService csrService,
            CsrRepository csrRepository,
            CsrPdfService csrPdfService) {

        this.csrService = csrService;
        this.csrRepository = csrRepository;
        this.csrPdfService = csrPdfService;
    }

    // ============================
    // Generate CSR & Save to DB
    // ============================
    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestBody CsrRequestDto request,
            Authentication authentication) {

        try {
            String username = (authentication != null)
                    ? authentication.getName()
                    : "api-user";

            String csr = csrService.generateCsr(request);

            CsrEntity entity = new CsrEntity();
            entity.setUsername(username);
            entity.setCommonName(request.getCommonName());
            entity.setEmail(request.getEmail());
            entity.setState(request.getState());
            entity.setPostalCode(request.getPostalCode());
            entity.setOrganizationalUnit(request.getOrganizationalUnit());
            entity.setOrganization(request.getOrganization());
            entity.setCountry(request.getCountry());
            entity.setRsaKeySize(request.getRsaKeySize());
            entity.setHashAlgorithm(request.getHashAlgorithm());
            entity.setCsrPem(csr);
            entity.setCreatedAt(LocalDateTime.now());

            CsrEntity saved = csrRepository.save(entity);

            return ResponseEntity.ok(
                    Map.of(
                            "id", saved.getId(),
                            "csr", csr
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("CSR generation failed");
        }
    }

    // ============================
    // Download CSR as PDF
    // ============================
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {

        try {
            CsrEntity csr = csrRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("CSR not found"));

            byte[] pdf = csrPdfService.generateCsrPdf(csr);

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=csr_" + id + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

