<!--
@startuml
start
:CAS Single Sign Out Filter;
if (request URI matches /user/*) then (yes)
:CAS Authentication Filter;
else (no)
endif
:CAS Validation Filter;
:CAS HTTP Request Wrapper;
:JWT Bearer Token AuthNZ;
if (request URI matches /user/* OR /rest/*) then (yes)
:Fedora Roles;
else (no)
endif
stop
@enduml
-->
