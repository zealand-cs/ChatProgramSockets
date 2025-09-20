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

    jdkVersion = "21";
  in {
    packages = eachSystem (
      pkgs: let
        jdk = pkgs."jdk${jdkVersion}";
        maven = pkgs.maven.override {jdk_headless = jdk;};
        pname = "socket-chat-server";
        jarName = "ChatProgramSockets-0.1.0";
      in {
        server = pkgs.maven.buildMavenPackage rec {
          inherit pname;
          version = "0.1.0";
          src = ./.;

          mvnParameters = "-Pclient";

          mvnHash = "sha256-vd1EqcRJlb8onwBbZ/oDxDoL/oPUwGPJFrcHzQpyilY=";

          nativeBuildInputs = [pkgs.makeWrapper];

          installPhase = ''
            mkdir -p $out/bin $out/share/${pname}
            install -Dm644 target/${jarName}.jar $out/share/${pname}

            makeWrapper ${jdk}/bin/java $out/bin/${pname} \
              --add-flags "-jar $out/share/${pname}/${jarName}.jar"
          '';
        };
      }
    );

    devShells = eachSystem (
      pkgs: let
        jdk = pkgs."jdk${jdkVersion}";

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
