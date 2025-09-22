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
        version = "0.1.0";
      in rec {
        default = server;

        server = pkgs.maven.buildMavenPackage rec {
          inherit version;
          pname = "chat-server";
          src = ./.;

          mvnJdk = jdk;
          mvnHash = "sha256-mdsv3HcsdeXkIMFH5PDt9qaV1Xorrz6oXcy1wDwCOoI=";
          mvnParameters = "-pl protocol,server -am";

          nativeBuildInputs = [pkgs.makeWrapper];

          installPhase = ''
            runHook preInstall

            mkdir -p $out/bin $out/share/${pname}
            install -Dm644 server/target/${pname}-${version}.jar $out/share/${pname}

            makeWrapper ${jdk}/bin/java $out/bin/${pname} \
              --add-flags "-jar $out/share/${pname}/${pname}-${version}.jar"

            runHook postInstall
          '';
        };

        client = pkgs.maven.buildMavenPackage rec {
          inherit version;
          pname = "chat-client";
          src = ./.;

          mvnJdk = jdk;
          mvnHash = "sha256-lo3fZOqmKDviz73xqsgfPjLEqOqdM2N4yTgjeIXNKek=";
          mvnParameters = "-pl protocol,client -am";

          nativeBuildInputs = [pkgs.makeWrapper];

          installPhase = ''
            runHook preInstall

            mkdir -p $out/bin $out/share/${pname}
            install -Dm644 client/target/${pname}-${version}.jar $out/share/${pname}

            makeWrapper ${jdk}/bin/java $out/bin/${pname} \
              --add-flags "-jar $out/share/${pname}/${pname}-${version}.jar"

            runHook postInstall
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
