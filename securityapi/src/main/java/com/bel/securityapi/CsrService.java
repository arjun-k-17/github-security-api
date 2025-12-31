package com.bel.securityapi;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.util.Base64;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.springframework.stereotype.Service;

@Service
public class CsrService {

    // ✅ Register Bouncy Castle ONCE
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public String generateCsr(CsrRequestDto dto) throws Exception {

        // ================= KEY PAIR =================
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(dto.getRsaKeySize());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // ================= SUBJECT =================
        String dn = String.format(
                "C=%s, O=%s, OU=%s, ST=%s, CN=%s, EMAILADDRESS=%s",
                dto.getCountry(),
                dto.getOrganization(),
                dto.getOrganizationalUnit(),
                dto.getState(),
                dto.getCommonName(),
                dto.getEmail()
        );

        X500Name subject = new X500Name(dn);

        PKCS10CertificationRequestBuilder builder =
                new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());

        // ================= SIGNATURE ALGORITHM (FIXED) =================
        String algorithm = switch (dto.getHashAlgorithm()) {
            case "SHA256" -> "SHA256WITHRSA";
            case "SHA384" -> "SHA384WITHRSA";
            case "SHA512" -> "SHA512WITHRSA";
            default -> "SHA256WITHRSA";
        };

        ContentSigner signer =
                new JcaContentSignerBuilder(algorithm)
                        .setProvider("BC")
                        .build(keyPair.getPrivate());

        // ================= CSR =================
        PKCS10CertificationRequest csr = builder.build(signer);

        String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'})
                .encodeToString(csr.getEncoded());

        return "-----BEGIN CERTIFICATE REQUEST-----\n"
                + base64
                + "\n-----END CERTIFICATE REQUEST-----";
    }
}
