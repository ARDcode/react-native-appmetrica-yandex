require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-appmetrica-yandex"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-appmetrica-yandex
                   DESC
  s.homepage     = "https://github.com/ARDcode/react-native-appmetrica-yandex"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.authors      = { "ARDCode" => "codeard@gmail.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/ARDcode/react-native-appmetrica-yandex.git" }

  s.source_files = "ios/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "YandexMobileMetrica", "3.8.2"

end

