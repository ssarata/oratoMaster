// package com.bestprogramer.bestprogramer.services;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.bestprogramer.bestprogramer.models.Certificat;
// import com.bestprogramer.bestprogramer.models.User;
// import com.bestprogramer.bestprogramer.repositories.CertificatRepository;

// import java.time.LocalDate;
// import java.util.UUID;

// @Service
// public class CertificatService {

//     @Autowired
//     private CertificatRepository certificatRepository;
    
//     // Dans un environnement réel, vous utiliseriez une bibliothèque comme iText ou PDFBox
//     // pour générer un vrai PDF de certificat
//     public Certificat genererCertificat(User user) {
//         Certificat certificat = new Certificat();
//         certificat.setUser(user);
//         certificat.setNumero(UUID.randomUUID().toString());
//         certificat.setDateEmission(LocalDate.now());
        
//         // Simuler la génération d'un fichier PDF
//         String fichierPdf = "certificat_" + user.getId() + "_" + UUID.randomUUID().toString() + ".pdf";
//         certificat.setFichierPdf(fichierPdf);
        
//         return certificatRepository.save(certificat);
//     }
// }
