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
in pkgs.mkShell {
  buildInputs = [
    openjdk
    vscode-with-extensions
  ];
}
