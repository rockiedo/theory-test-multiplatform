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

    // Specify the destination path for the database in the Documents directory
    let destinationURL = documentsDirectory.appendingPathComponent("app.db")

    // Check if the database file already exists in the Documents directory
    if !FileManager.default.fileExists(atPath: destinationURL.path) {
        // If it doesn't exist, copy it from the app bundle
        if let bundleURL = Bundle.main.url(forResource: "app", withExtension: "db") {
            do {
                try FileManager.default.copyItem(at: bundleURL, to: destinationURL)
            } catch {
                print("Error copying database: \(error)")
            }
        }
    }
    
    return documentsDirectory.path
}
