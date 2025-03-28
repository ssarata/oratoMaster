
async function main() {
  try {
    const genAI = new GoogleGenerativeAI("AIzaSyAPUN8UP3Givgc_00EHjeod7nvbXFmsPi4");
    const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });
    const prompt = "How does AI work?";

    const result = await model.generateContent(prompt);

    console.log("Response:", result);  // 🔍 Voir ce que l'API renvoie

    if (result && result.response) {
      console.log("Generated Text:", result.response.text());
    } else {
      console.error("No response received!");
    }
  } catch (error) {
    console.error("Error:", error);
  }
}

main();
