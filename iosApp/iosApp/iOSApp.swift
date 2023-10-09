import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        let dbPath = extractDb()
        DiKt.doInitKoin(dbName: "app.db", basePath: dbPath)
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}

func extractDb() -> String {
    // Get the path to the Documents directory
    let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    let outputFile = documentsDirectory.appendingPathComponent("database/app.db")

    // Check if the database file already exists in the Documents directory
    if !FileManager.default.fileExists(atPath: outputFile.path) {
        // If it doesn't exist, copy it from the app bundle
        if let bundlePath = Bundle.main.resourcePath {
            let resourceDirectory = URL(fileURLWithPath: bundlePath)
            let inputFile = resourceDirectory.appendingPathComponent("database/app.db")
            
            do {
                try FileManager.default.createDirectory(
                    at: outputFile.deletingLastPathComponent(),
                    withIntermediateDirectories: true,
                    attributes: nil
                )
                
                try FileManager.default.copyItem(at: inputFile, to: outputFile)
            } catch {
                print("Error copying database: \(error)")
            }
        }
    }
    
    return outputFile.deletingLastPathComponent().path
}
