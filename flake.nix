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
        buildMvnPkg = {
          pname,
          mvnParameters ? "",
        }:
          pkgs.maven.buildMavenPackage rec {
            inherit pname version mvnParameters;
            src = ./.;

            mvnJdk = jdk;
            mvnHash = "sha256-vd1EqcRJlb8onwBbZ/oDxDoL/oPUwGPJFrcHzQpyilY=";

            nativeBuildInputs = [pkgs.makeWrapper];

            installPhase = ''
              runHook preInstall

              mkdir -p $out/bin $out/share/${pname}
              mkdir -p $out/bin $out/share/${pname}
              install -Dm644 target/${pname}.jar $out/share/${pname}

              makeWrapper ${jdk}/bin/java $out/bin/${pname} \
                --add-flags "-jar $out/share/${pname}/${pname}.jar"

              runHook postInstall
            '';
          };
      in {
        default = buildMvnPkg {
          inherit version;
          pname = "socket-chat";
        };

        server = buildMvnPkg {
          pname = "chat-server";
          mvnParameters = "-pl server -amd";
        };

        client = buildMvnPkg {
          pname = "chat-client";
          mvnParameters = "-pl protocol,server -amd";
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
