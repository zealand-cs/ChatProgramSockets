{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
  };

  outputs = {
    self,
    nixpkgs,
  }: let
    systems = ["x86_64-linux"];

    eachSystem = function:
      nixpkgs.lib.genAttrs systems
      (system: function nixpkgs.legacyPackages.${system});
  in {
    devShells = eachSystem (
      pkgs: let
        jdk-version = "21";
        jdk = pkgs."jdk${jdk-version}";

        maven = pkgs.maven.override {jdk_headless = jdk;};
        gradle = pkgs.gradle.override {java = jdk;};
      in {
        default = with pkgs;
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
  };
}
