#----------------------------------------------------------------
# Generated CMake target import file.
#----------------------------------------------------------------

# Commands may need to know the format version.
set(CMAKE_IMPORT_FILE_VERSION 1)

# Import target "Expresso::Expresso" for configuration ""
set_property(TARGET Expresso::Expresso APPEND PROPERTY IMPORTED_CONFIGURATIONS NOCONFIG)
set_target_properties(Expresso::Expresso PROPERTIES
  IMPORTED_LINK_INTERFACE_LANGUAGES_NOCONFIG "C"
  IMPORTED_LOCATION_NOCONFIG "${_IMPORT_PREFIX}/lib/libExpresso.a"
  )

list(APPEND _cmake_import_check_targets Expresso::Expresso )
list(APPEND _cmake_import_check_files_for_Expresso::Expresso "${_IMPORT_PREFIX}/lib/libExpresso.a" )

# Commands beyond this point should not need to know the version.
set(CMAKE_IMPORT_FILE_VERSION)
