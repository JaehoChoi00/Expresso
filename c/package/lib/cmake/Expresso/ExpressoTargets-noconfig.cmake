#----------------------------------------------------------------
# Generated CMake target import file.
#----------------------------------------------------------------

# Commands may need to know the format version.
set(CMAKE_IMPORT_FILE_VERSION 1)

# Import target "Expresso::expresso" for configuration ""
set_property(TARGET Expresso::expresso APPEND PROPERTY IMPORTED_CONFIGURATIONS NOCONFIG)
set_target_properties(Expresso::expresso PROPERTIES
  IMPORTED_LINK_INTERFACE_LANGUAGES_NOCONFIG "C"
  IMPORTED_LOCATION_NOCONFIG "${_IMPORT_PREFIX}/lib/libexpresso.a"
  )

list(APPEND _cmake_import_check_targets Expresso::expresso )
list(APPEND _cmake_import_check_files_for_Expresso::expresso "${_IMPORT_PREFIX}/lib/libexpresso.a" )

# Commands beyond this point should not need to know the version.
set(CMAKE_IMPORT_FILE_VERSION)
