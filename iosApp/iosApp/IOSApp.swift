import SwiftUI
import FirebaseCore
import FirebaseFirestore
import FirebaseCrashlytics
import GoogleSignIn
import ComposeApp
import VisionKit
import Vision
import AVFoundation

@main
struct IOSApp: App {
    @State private var isComposeReady = false

    init() {
        #if DEBUG
        FirebaseConfiguration.shared.setLoggerLevel(.debug)
        #endif
        FirebaseApp.configure()
        GoogleSignInBridge.shared.launcher = NativeGoogleSignInLauncher()
        EventScannerBridge.shared.launcher = NativeEventScannerLauncher()
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
        Crashlytics.crashlytics().setCustomValue("ios", forKey: "platform")
        Crashlytics.crashlytics().log("IOSApp initialized")
        #if DEBUG
        Firestore.enableLogging(true)
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ZStack {
                ComposeAppView(
                    onComposeReady: {
                        withAnimation(.easeOut(duration: 0.2)) {
                            isComposeReady = true
                        }
                    }
                )
                .ignoresSafeArea(.all, edges: .all)

                if !isComposeReady {
                    NativeLaunchPlaceholderView()
                        .transition(.opacity)
                }
            }
            .onOpenURL { url in GIDSignIn.sharedInstance.handle(url) }
        }
    }
}

private final class NativeGoogleSignInLauncher: NSObject, GoogleSignInLauncher {
    func launch(completion: GoogleSignInCallback) {
        guard let clientID = FirebaseApp.app()?.options.clientID,
              let scene = UIApplication.shared.connectedScenes.first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
              var presenter = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController else {
            completion.complete(token: nil, accessToken: nil, error: "Google ile giriş henüz yapılandırılmadı.")
            return
        }
        let reversedID = clientID.components(separatedBy: ".").reversed().joined(separator: ".")
        let urlTypes = Bundle.main.object(forInfoDictionaryKey: "CFBundleURLTypes") as? [[String: Any]] ?? []
        guard urlTypes.contains(where: { ($0["CFBundleURLSchemes"] as? [String])?.contains(reversedID) == true }) else {
            completion.complete(token: nil, accessToken: nil, error: "Google ile giriş henüz yapılandırılmadı.")
            return
        }
        while let presented = presenter.presentedViewController { presenter = presented }
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.signIn(withPresenting: presenter) { result, error in
            DispatchQueue.main.async {
                if let token = result?.user.idToken?.tokenString { completion.complete(token: token, accessToken: result?.user.accessToken.tokenString, error: nil) }
                else if let signInError = error as NSError?,
                        signInError.domain == kGIDSignInErrorDomain,
                        signInError.code == GIDSignInError.canceled.rawValue { completion.complete(token: nil, accessToken: nil, error: nil) }
                else { completion.complete(token: nil, accessToken: nil, error: "Google ile giriş tamamlanamadı. Tekrar deneyin.") }
            }
        }
    }
}

private final class NativeEventScannerLauncher: NSObject, EventScannerLauncher {
    func launch(completion_ completion: EventScannerCallback) {
        guard DataScannerViewController.isSupported else {
            completion.complete(value: nil, error: "Bu cihazda kamera taraması desteklenmiyor. Simülatörde manuel giriş kullanabilirsiniz.")
            return
        }
        AVCaptureDevice.requestAccess(for: .video) { allowed in
            DispatchQueue.main.async {
                guard allowed, DataScannerViewController.isAvailable else {
                    completion.complete(value: nil, error: "Kamera izni gerekli. Ayarlar’dan kamera iznini açabilir veya manuel giriş kullanabilirsiniz.")
                    return
                }
                guard let scene = UIApplication.shared.connectedScenes.first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
                      var presenter = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController else {
                    completion.complete(value: nil, error: "Kamera açılamadı.")
                    return
                }
                while let presented = presenter.presentedViewController { presenter = presented }
                let controller = EventScannerController(completion: completion)
                let navigation = UINavigationController(rootViewController: controller)
                navigation.modalPresentationStyle = .fullScreen
                presenter.present(navigation, animated: true)
            }
        }
    }
}

private final class EventScannerController: UIViewController, DataScannerViewControllerDelegate {
    private let scanner = DataScannerViewController(recognizedDataTypes: [.barcode(symbologies: [.qr])],
        qualityLevel: .balanced, recognizesMultipleItems: false, isGuidanceEnabled: true, isHighlightingEnabled: true)
    private var completion: EventScannerCallback?
    init(completion: EventScannerCallback) { self.completion = completion; super.init(nibName: nil, bundle: nil) }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Öğrencinin QR biletini okut"
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Kapat", style: .plain, target: self, action: #selector(cancel))
        addChild(scanner)
        view.addSubview(scanner.view)
        scanner.view.frame = view.bounds
        scanner.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        scanner.didMove(toParent: self)
        scanner.delegate = self
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        do { try scanner.startScanning() }
        catch { finish(value: nil, error: "Kamera başlatılamadı. Manuel giriş kullanabilirsiniz.") }
    }
    @objc private func cancel() { finish(value: nil, error: nil) }
    private func finish(value: String?, error: String?) {
        guard let callback = completion else { return }
        completion = nil
        scanner.stopScanning()
        dismiss(animated: true) { callback.complete(value: value, error: error) }
    }
    func dataScanner(_ dataScanner: DataScannerViewController, didAdd addedItems: [RecognizedItem], allItems: [RecognizedItem]) {
        for item in addedItems {
            if case .barcode(let barcode) = item, let value = barcode.payloadStringValue {
                finish(value: value, error: nil)
                break
            }
        }
    }
    func dataScanner(_ dataScanner: DataScannerViewController, becameUnavailableWithError error: DataScannerViewController.ScanningUnavailable) {
        finish(value: nil, error: "Kamera kullanılamıyor. Manuel giriş kullanabilirsiniz.")
    }
}

private struct NativeLaunchPlaceholderView: View {
    var body: some View {
        ZStack {
            Color(red: 248.0 / 255.0, green: 247.0 / 255.0, blue: 244.0 / 255.0)
                .ignoresSafeArea()

            VStack(spacing: 16) {
                Image("SplashLogo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 120, height: 120)
                ProgressView()
                    .progressViewStyle(.circular)
                    .tint(Color.black)
            }
        }
    }
}
