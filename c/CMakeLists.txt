cmake_minimum_required(VERSION 3.20)

project(Expresso
    VERSION 0.0.1
    LANGUAGES C
)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(CMAKE_C_EXTENSIONS OFF)

include(GNUInstallDirs)
include(CMakePackageConfigHelpers)

find_package(Threads REQUIRED)

add_library(expresso STATIC
    src/Expresso.c
    src/Exposure.c
    src/ExposureCategory.c
)

target_include_directories(expresso
    PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
        $<INSTALL_INTERFACE:${CMAKE_INSTALL_INCLUDEDIR}>
)

target_link_libraries(expresso
    PUBLIC
        Threads::Threads
)

enable_testing()

add_executable(ExpressoTest
    test/ExpressoTest.c
)

target_link_libraries(ExpressoTest
    PRIVATE
        expresso
)

add_executable(LibraryTest
    test/LibraryTest.c
)

target_link_libraries(LibraryTest
    PRIVATE
        expresso
)

add_executable(ThreadTest
    test/ThreadTest.c
)

target_link_libraries(ThreadTest
    PRIVATE
        expresso
)

add_test(NAME ExpressoTest COMMAND ExpressoTest)
add_test(NAME LibraryTest COMMAND LibraryTest)
add_test(NAME ThreadTest COMMAND ThreadTest)

install(
    TARGETS expresso
    EXPORT ExpressoTargets
    ARCHIVE DESTINATION ${CMAKE_INSTALL_LIBDIR}
)

install(
    DIRECTORY include/
    DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}
)

install(
    EXPORT ExpressoTargets
    FILE ExpressoTargets.cmake
    NAMESPACE Expresso::
    DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/Expresso
)

configure_package_config_file(
    ${CMAKE_CURRENT_SOURCE_DIR}/cmake/ExpressoConfig.cmake.in
    ${CMAKE_CURRENT_BINARY_DIR}/ExpressoConfig.cmake
    INSTALL_DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/Expresso
)

write_basic_package_version_file(
    ${CMAKE_CURRENT_BINARY_DIR}/ExpressoConfigVersion.cmake
    VERSION ${PROJECT_VERSION}
    COMPATIBILITY SameMajorVersion
)

install(
    FILES
        ${CMAKE_CURRENT_BINARY_DIR}/ExpressoConfig.cmake
        ${CMAKE_CURRENT_BINARY_DIR}/ExpressoConfigVersion.cmake
    DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/Expresso
)