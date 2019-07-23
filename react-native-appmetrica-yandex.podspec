require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-appmetrica-yandex"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-appmetrica-yandex
                   DESC
  s.homepage     = "https://github.com/github_account/react-native-yandex-appmetrica"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.authors      = { "Your Name" => "yourname@email.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/github_account/react-native-yandex-appmetrica.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "YandexMobileMetrica", "3.7.1"

end

