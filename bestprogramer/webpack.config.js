import path from "path"

export default {
  entry: "./src/main/resources/static/js/test.js", // Point d'entrée
  output: {
    path: path.resolve("src/main/resources/static/js"), // Dossier de sortie
    filename: "bundle.js", // Nom du fichier compilé
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
          options: {
            presets: ["@babel/preset-env"],
          },
        },
      },
    ],
  },
  mode: "development", // Change en 'production' pour une version finale
}
