cmake_minimum_required(VERSION 3.20)
project(RuthraMart CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find Drogon and dependencies
find_package(Drogon CONFIG QUIET)
find_package(SQLite3 QUIET)

include_directories(
    backend
    backend/controllers
    backend/models
    backend/services
    backend/middleware
    backend/utils
    backend/config
)

file(GLOB_RECURSE SOURCES 
    "backend/*.cpp"
    "backend/*.h"
)

if (TARGET Drogon::Drogon)
    add_executable(ruthramart_server ${SOURCES})
    target_link_libraries(ruthramart_server PRIVATE Drogon::Drogon)
    if (SQLite3_FOUND)
        target_link_libraries(ruthramart_server PRIVATE SQLite::SQLite3)
    endif()
else()
    message(STATUS "Drogon package not found on host. RuthraMart Java Spring Boot backend is the primary runtime engine.")
endif()
