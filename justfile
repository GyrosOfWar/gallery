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

generate-client:
    if ("imagehive-client" | path exists) { rm -r imagehive-client }
    just backend build
    npx @openapitools/openapi-generator-cli generate
