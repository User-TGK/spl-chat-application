with ((import (fetchTarball {
  name = "nixpkgs-master-2021-09-08";
  url = "https://github.com/nixos/nixpkgs/archive/739c25621f94f0e892e5ed33ba4f2196ccc05e64.tar.gz";
  sha256 = "0r2fswip9gzhly16yrcyq3v2h13gpyvqs155f39imjdyl1dk767w";
}) {}));
let
  extensions = (with pkgs.vscode-extensions; [
    ms-vsliveshare.vsliveshare
  ]);
  vscode-with-extensions = pkgs.vscode-with-extensions.override {
    vscodeExtensions = extensions;
  };
  featureIDE = stdenv.mkDerivation {
    pname = "FeatureIDE";
    version = "3.8.0";

    src = fetchzip {
     url = "https://github.com/FeatureIDE/FeatureIDE/releases/download/v3.8.0/eclipse4.20.0committers-featureide3.8.0-linux64.zip";
     sha256 = "1h1zgzvsjr2y1yh486p54z7zj6igyfpdr09cl039vi1k810jwywn";
     stripRoot = false; 
   };

   installPhase = ''
     mkdir -p $out/bin
     cp -r eclipse $out/eclipse
     foo="$XDG_ICON_DIRS:$GSETTINGS_SCHEMAS_PATH\''${XDG_DATA_DIRS:+:}\$XDG_DATA_DIRS"
     makeWrapper $out/eclipse/eclipse $out/bin/eclipse \
       --prefix PATH : ${lib.makeBinPath [ pkgs.openjdk ]} \
   '';

   buildInputs = [
     pkgs.gtk3
     pkgs.cairo
     pkgs.webkitgtk
     pkgs.openjdk
     pkgs.makeWrapper
   ];

   nativeBuildInputs = [
     pkgs.wrapGAppsHook
     pkgs.autoPatchelfHook
   ];
  };

in pkgs.mkShell {
  buildInputs = [
    vscode-with-extensions
    featureIDE
  ];
}
