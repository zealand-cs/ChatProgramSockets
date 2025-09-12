{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    utils.url = "github:numtide/flake-utils";
  };

  outputs = {
    self,
    nixpkgs,
    utils,
  }: let
    supportedSystems = ["x86_64-linux"];
    eachSystem = {
    };
  in
    utils.lib.eachDefaultSystem (
      system: let
        pkgs = import nixpkgs {inherit system;};

        jdk-version = "21";
        jdk = pkgs."jdk${jdk-version}";

        maven = pkgs.maven.override {jdk_headless = jdk;};
        gradle = pkgs.gradle.override {java = jdk;};
      in {
        devShell = with pkgs;
          mkShell {
            nativeBuildInputs = [pkg-config];
            buildInputs = [
              jdk
              maven
              gradle
            ];

            JAVA_HOME = jdk;
            JDK_HOME = jdk;
            JRE_HOME = "${jdk}/jre";
            CLASS_PATH = ".;${jdk}/lib;${jdk}/jre/lib";
          };
      }
    );
}
