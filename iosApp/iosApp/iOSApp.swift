import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        let dbDir = extractDb()
        let imageDir = getImageDir()
        DiKt.doInitKoin(dbName: "app.db", dbDir: dbDir, imageDir: imageDir)
    }
    
    var body: some Scene {
        WindowGroup {
            ZStack {
                Color("CustomBg").ignoresSafeArea()
                ContentView()
            }
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

func getImageDir() -> String {
    if let bundlePath = Bundle.main.resourcePath {
        let imageDir = URL(fileURLWithPath: bundlePath).appendingPathComponent("images")
        return imageDir.path
    }
    
    return ""
}

extension Color {
    init?(hex: String) {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")

        var rgb: UInt64 = 0

        var r: CGFloat = 0.0
        var g: CGFloat = 0.0
        var b: CGFloat = 0.0
        var a: CGFloat = 1.0

        let length = hexSanitized.count

        guard Scanner(string: hexSanitized).scanHexInt64(&rgb) else { return nil }

        if length == 6 {
            r = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
            g = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
            b = CGFloat(rgb & 0x0000FF) / 255.0

        } else if length == 8 {
            r = CGFloat((rgb & 0xFF000000) >> 24) / 255.0
            g = CGFloat((rgb & 0x00FF0000) >> 16) / 255.0
            b = CGFloat((rgb & 0x0000FF00) >> 8) / 255.0
            a = CGFloat(rgb & 0x000000FF) / 255.0

        } else {
            return nil
        }

        self.init(red: r, green: g, blue: b, opacity: a)
    }
}
