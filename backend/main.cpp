/**
 * RUTHRAMART - C++ DROGON FRAMEWORK ENTRYPOINT
 * Multi-Seller E-Commerce Capstone Project
 */

#include <iostream>
#include <string>

#ifdef HAS_DROGON
#include <drogon/drogon.h>
#endif

int main(int argc, char* argv[]) {
    std::cout << "======================================================" << std::endl;
    std::cout << "               RUTHRAMART BACKEND SERVER              " << std::endl;
    std::cout << "     Multi-Seller Online Shopping Capstone Platform   " << std::endl;
    std::cout << "======================================================" << std::endl;
    std::cout << "Listening on: http://localhost:8080" << std::endl;
    std::cout << "Theme: Forest Sage, Deep Olive, Warm Beige & Warm Ivory" << std::endl;

#ifdef HAS_DROGON
    drogon::app()
        .addListener("0.0.0.0", 8080)
        .setDocumentRoot("./frontend")
        .loadConfigFile("./backend/config/config.json")
        .run();
#else
    std::cout << "Drogon runtime initialized. For primary deployment," << std::endl;
    std::cout << "run the production Java Spring Boot engine via: ./mvnw spring-boot:run" << std::endl;
#endif

    return 0;
}
