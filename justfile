set shell := ["nu", "-c"]

project := justfile_directory()
frontend := project + "/frontend"
backend := project + "/backend"
ai := project + "/imagehive-ai"
codegen := project + "/imagehive-codegen"

@backend *cmd:
    cd {{backend}}; just {{cmd}}

@frontend *cmd:
    cd {{frontend}}; just {{cmd}}

@ai *cmd:
    cd {{ai}}; just {{cmd}}

@codegen *cmd:
    cd {{codegen}}; just {{cmd}}

format:
  just backend format
  just frontend format
  just ai format
  just codegen format

